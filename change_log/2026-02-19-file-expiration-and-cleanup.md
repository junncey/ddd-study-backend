# 文件过期时间和定时清理功能

## 变更日期
2026-02-19

## 变更内容

### 1. 文件过期时间机制

#### 1.1 过期时间配置（StorageProperties）
- 新增 `pendingExpireDays` 配置项：临时文件/已删除文件过期天数（默认3天）
- 新增 `boundExpireDays` 配置项：已绑定业务文件过期天数（默认730天=2年）

#### 1.2 FileInfo 实体修改
- 修改 `markDeleted()` 方法：设置过期时间为3天后
- 新增 `markDeleted(int expireDays)` 方法：支持自定义过期天数
- 修改 `bindBusiness()` 方法：绑定业务时设置2年后过期
- 新增 `bindBusiness(BizType bizType, Long bizId, int expireDays)` 方法：支持自定义过期天数

#### 1.3 过期规则
| 文件状态 | 过期时间 |
|---------|---------|
| PENDING（待绑定/临时文件） | 3天 |
| DELETED（已删除） | 3天 |
| BOUND（已绑定业务） | 2年 |

### 2. 定时清理任务

#### 2.1 新增 FileCleanupScheduler
- 位置：`infrastructure/scheduler/FileCleanupScheduler.java`
- 执行时间：每天凌晨 02:00（cron: `0 0 2 * * ?`）
- 功能：
  - 查询所有过期文件（PENDING 和 DELETED 状态）
  - 物理删除磁盘文件
  - 逻辑删除数据库记录

#### 2.2 FileRepository 新增方法
- `findExpiredFiles()`：查询所有过期文件（包括临时和已删除的）

#### 2.3 FileApplicationService 修改
- `cleanExpiredFiles()` 方法：支持物理删除磁盘文件 + 逻辑删除数据库记录
- `deleteFile()` 方法：改为标记删除，不立即删除物理文件
- `deleteByBusiness()` 方法：改为标记删除，不立即删除物理文件

### 3. 商品多图片上传和绑定

#### 3.1 ProductCreateRequest 修改
- 新增 `imageFileKeys` 字段：商品图片文件key列表

#### 3.2 ProductUpdateRequest 修改
- 新增 `imageFileKeys` 字段：商品图片文件key列表

#### 3.3 FileController 新增接口
- `POST /api/files/product-images`：专门用于商品图片上传（支持多图片）

#### 3.4 ProductApplicationService 修改
- `createProductFromRequest()`：创建商品时绑定图片文件
- `updateProductFromRequest()`：更新商品时绑定图片文件

## 使用流程

### 商品图片上传流程

1. **上传图片**
```bash
POST /api/files/product-images
Content-Type: multipart/form-data

files: [图片文件列表]
```

响应：
```json
{
  "code": 200,
  "data": [
    { "fileKey": "abc123...", "url": "http://...", "fileName": "image1.jpg" },
    { "fileKey": "def456...", "url": "http://...", "fileName": "image2.jpg" }
  ]
}
```

2. **创建商品时绑定图片**
```bash
POST /api/products
Content-Type: application/json

{
  "shopId": 1,
  "categoryId": 1,
  "productName": "商品名称",
  "productDesc": "商品描述",
  "mainImage": "主图URL",
  "imageFileKeys": ["abc123...", "def456..."],
  "price": 100.00,
  "stock": 100
}
```

3. **更新商品时替换图片**
```bash
PUT /api/products/1
Content-Type: application/json

{
  "productName": "新商品名称",
  "imageFileKeys": ["新的fileKey1", "新的fileKey2"]
}
```

## 测试验证

### 编译验证
```bash
mvn clean compile
```
结果：编译成功

### 功能测试（2026-02-19 执行）

#### 1. 文件上传测试
```bash
curl -X POST "http://localhost:8080/api/files/upload" \
  -H "Authorization: Bearer <token>" \
  -F "file=@test_image.png"
```
结果：✅ 成功，返回 fileKey

#### 2. 过期时间验证（MySQL查询）
```sql
SELECT file_key, status, expire_time, create_time FROM t_file;
```

| 场景 | 状态 | 创建时间 | 过期时间 | 结果 |
|------|------|----------|----------|------|
| 临时文件 | PENDING(0) | 2026-02-19 12:32:58 | 2026-02-22 12:32:58 | ✅ 3天 |
| 绑定业务 | BOUND(1) | 2026-02-19 12:32:58 | 2028-02-19 12:33:31 | ✅ 2年 |
| 标记删除 | DELETED(2) | 2026-02-19 12:32:58 | 2026-02-22 12:33:58 | ✅ 3天 |

#### 3. 用户登录测试（验证码从Redis获取）
- 调用 `/api/auth/captcha` 获取 captchaKey
- 从 Redis `GET captcha:{captchaKey}` 获取验证码
- 使用验证码登录成功 ✅

#### 4. 待验证项
- ⏳ 定时任务清理（需等待每天02:00或手动触发）

## 影响范围

### 修改文件
1. `infrastructure/storage/StorageProperties.java` - 新增过期时间配置
2. `domain/model/entity/FileInfo.java` - 修改过期时间逻辑
3. `domain/repository/FileRepository.java` - 新增查询方法
4. `infrastructure/persistence/repository/FileRepositoryImpl.java` - 实现查询方法
5. `application/service/FileApplicationService.java` - 修改清理和删除逻辑
6. `infrastructure/scheduler/FileCleanupScheduler.java` - 新建定时任务
7. `interfaces/rest/controller/FileController.java` - 新增商品图片上传接口
8. `interfaces/rest/dto/ProductCreateRequest.java` - 新增图片字段
9. `interfaces/rest/dto/ProductUpdateRequest.java` - 新增图片字段
10. `application/service/ProductApplicationService.java` - 修改商品创建/更新逻辑

## 注意事项

1. 定时任务每天02:00运行，过期文件不会立即删除
2. 文件删除是异步的，前端展示时需要考虑文件可能已过期的情况
3. 已绑定业务的文件2年后才会过期，长期存储的业务数据需关注
4. 商品更新时会替换原有图片，旧图片会被标记删除，3天后过期清理
