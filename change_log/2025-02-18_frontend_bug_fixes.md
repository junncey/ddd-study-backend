# 前端Bug修复报告

## 日期
2025-02-18

## 问题描述

经人工测试，前端存在以下bug：
1. 管理员订单管理页面存在问题，接口报错
2. 商品分页查询价格等文字信息显示存在问题

## 问题分析与修复

### 1. 管理员订单管理页面接口报错

**问题原因：**
- 前端调用 `/orders/shop?shopId=1` (查询参数)
- 后端定义 `/orders/shop/{shopId}` (路径参数)
- URL不匹配导致404错误

**修复方案：**
修改 `frontend/src/pages/shop-admin/OrderManage/index.tsx`，将API调用从查询参数改为路径参数：
```typescript
// 修复前
const response = await request.get(`/orders/shop?${params.toString()}`);

// 修复后
let url = `/orders/shop/${shopId}`;
if (params.toString()) {
    url += `?${params.toString()}`;
}
const response = await request.get(url);
```

同时修复了分页响应的处理，正确提取 `records` 数组。

### 2. 商品价格和库存字段缺失

**问题原因：**
- 数据库设计中，价格和库存存储在 `t_product_sku` 表中
- `t_product` 表没有 `price` 和 `stock` 字段
- 前端期望 Product 对象直接有这些字段

**修复方案：**

**后端修改：**
1. 在 `Product.java` 实体中添加非持久化字段：
```java
@TableField(exist = false)
private BigDecimal minPrice;

@TableField(exist = false)
private Integer totalStock;

@TableField(exist = false)
private String categoryName;
```

2. 在 `ProductApplicationService.java` 中添加 `fillProductPriceAndStock` 方法，在返回商品列表时填充价格和库存信息。

**前端修改：**
1. 更新 `frontend/src/api/index.ts` 中的 Product 接口定义
2. 修改商品列表页面使用 `minPrice` 字段替代 `price`

### 3. 商品管理页面状态显示问题

**问题原因：**
- 后端返回 `status: "ON_SALE"` 字符串格式
- 前端 `extractStatusValue` 函数只处理数字和对象格式

**修复方案：**
修改 `frontend/src/pages/shop-admin/ProductManage/index.tsx` 中的 `extractStatusValue` 函数，添加字符串状态的处理：
```typescript
if (typeof status === 'string') {
    const statusMap: Record<string, number> = {
        'DRAFT': 0,
        'ON_SALE': 1,
        'SOLD_OUT': 2,
        'OFF_SALE': 3,
    };
    return statusMap[status] ?? 0;
}
```

### 4. 订单管理页面金额显示问题

**问题原因：**
- 后端可能返回值对象格式的金额 `{value: 100}`
- 前端直接调用 `toFixed()` 导致错误

**修复方案：**
修改 `frontend/src/pages/shop-admin/OrderManage/index.tsx`，添加 `extractNumber` 辅助函数处理各种格式的数值。

## 修改的文件列表

### 后端
1. `src/main/java/com/example/ddd/domain/model/entity/Product.java` - 添加 minPrice, totalStock, categoryName 字段
2. `src/main/java/com/example/ddd/application/service/ProductApplicationService.java` - 添加 fillProductPriceAndStock 方法

### 前端
1. `frontend/src/api/index.ts` - 更新 Product 接口定义
2. `frontend/src/pages/consumer/Home/index.tsx` - 使用 minPrice 字段
3. `frontend/src/pages/consumer/ProductList/index.tsx` - 使用 minPrice 字段
4. `frontend/src/pages/shop-admin/OrderManage/index.tsx` - 修复API URL和金额显示
5. `frontend/src/pages/shop-admin/ProductManage/index.tsx` - 修复状态显示和字段名

## 测试结果

修复后验证：
1. **首页商品列表** - 价格正确显示（¥5999.00, ¥2999.00）
2. **商品管理页面** - 价格、库存、状态正确显示
3. **订单管理页面** - 订单列表正常显示，金额正确

## 注意事项

1. 需要在数据库中创建店铺数据才能正常使用商家管理功能
2. 后端需要重新编译部署才能生效
