# 购物车 Bug 修复日志

## 日期
2026-02-18

## 问题描述

### Bug 1: 商品加入购物车后购物车角标不同步
- **现象**：在商品详情页加入购物车后，顶部购物车角标数字不更新
- **原因**：加入购物车成功后只调用了 `invalidateQueries`，但 `useCartStore` 状态未同步更新

### Bug 2: 商品从购物车删除后，再次加入购物车会报错
- **现象**：删除购物车商品后，再次添加同一商品时数据库报唯一索引冲突
- **原因**：数据库表 `t_cart_item` 的唯一索引 `uk_cart_sku(cart_id, sku_id)` 不包含 `deleted` 字段，逻辑删除的记录仍占用唯一索引

### Bug 3: 同一商品第二次删除时报错（Bug 2 修复后衍生问题）
- **现象**：同一商品第二次删除时接口返回 500 错误"系统繁忙"
- **原因**：修改唯一索引为 `uk_cart_sku_deleted(cart_id, sku_id, deleted)` 后，第二次逻辑删除时会与已存在的旧记录 (deleted=1) 产生唯一索引冲突
- **错误详情**：`Duplicate entry '5-2-1' for key 't_cart_item.uk_cart_sku_deleted'`

### 最终解决方案
购物车删除改用**物理删除**，直接从数据库删除记录，不使用逻辑删除。

## 修复方案

### Bug 1 修复
**修改文件**：`frontend/src/pages/consumer/ProductDetail/index.tsx`

**修改内容**：
1. 导入 `useCartStore`
2. 在 `addToCartMutation` 的 `onSuccess` 回调中添加 `addItem()` 调用
3. 同步更新本地购物车状态，确保角标立即刷新

**代码变更**：
```typescript
// 添加导入
import { useCartStore } from '../../../store/cart';

// 获取 addItem 方法
const { addItem } = useCartStore();

// 在 onSuccess 中同步更新状态
onSuccess: (data) => {
  message.success('已添加到购物车');
  queryClient.invalidateQueries({ queryKey: ['cart'] });
  // 同步更新本地购物车状态
  addItem({
    id: data.id,
    skuId: data.skuId,
    productId: data.productId,
    productName: product?.productName || '商品',
    skuName: selectedSku?.skuName || '默认规格',
    mainImage: product?.mainImage || '',
    price: extractValue(selectedSku?.price || product?.price) as number,
    quantity: quantity,
    selected: true,
  });
},
```

### Bug 2 修复
**新增文件**：`src/main/resources/db/migration/V1.0.1__fix_cart_item_unique_index.sql`

**修改内容**：
- 删除旧唯一索引 `uk_cart_sku(cart_id, sku_id)`
- 创建新唯一索引 `uk_cart_sku_deleted(cart_id, sku_id, deleted)`

**SQL 变更**：
```sql
ALTER TABLE `t_cart_item` DROP INDEX `uk_cart_sku`;
ALTER TABLE `t_cart_item` ADD UNIQUE INDEX `uk_cart_sku_deleted` (`cart_id`, `sku_id`, `deleted`);
```

### Bug 3 修复（最终方案）
购物车删除改用**物理删除**，避免唯一索引冲突。

**修改文件**：
- `CartItemRepository.java` - 添加 `physicalDeleteById` 接口
- `CartItemRepositoryImpl.java` - 实现物理删除方法
- `CartItemMapper.java` - 添加物理删除 SQL
- `CartDomainService.java` - `removeItem` 改用物理删除

**代码变更**：
```java
// CartItemMapper.java
@Delete("DELETE FROM t_cart_item WHERE id = #{id}")
int physicalDeleteById(@Param("id") Long id);

// CartDomainService.java
public boolean removeItem(Long cartItemId) {
    validate();
    CartItem cartItem = cartItemRepository.findById(cartItemId);
    if (cartItem == null) {
        return false;
    }
    // 使用物理删除，直接从数据库删除记录
    return cartItemRepository.physicalDeleteById(cartItemId) > 0;
}
```

## 数据库迁移

如果数据库已存在，需要手动执行迁移 SQL：

```bash
mysql -P 3307 -u root -proot ddd_demo < src/main/resources/db/migration/V1.0.1__fix_cart_item_unique_index.sql
```

或者在 MySQL 客户端中执行：
```sql
ALTER TABLE `t_cart_item` DROP INDEX `uk_cart_sku`;
ALTER TABLE `t_cart_item` ADD UNIQUE INDEX `uk_cart_sku_deleted` (`cart_id`, `sku_id`, `deleted`);
```

## 测试验证

### Bug 1 测试步骤
1. 登录系统
2. 进入任意商品详情页
3. 点击"加入购物车"
4. 观察顶部购物车角标是否立即更新

### Bug 2 测试步骤
1. 登录系统
2. 添加商品到购物车
3. 进入购物车，删除该商品
4. 返回商品详情页，再次添加同一商品
5. 验证是否成功添加，无报错

## 影响范围

| 文件 | 修改类型 |
|------|---------|
| `frontend/src/pages/consumer/ProductDetail/index.tsx` | 修改 - 角标同步 |
| `src/main/resources/db/migration/V1.0.1__fix_cart_item_unique_index.sql` | 新增 - 索引修复 |
| `CartItemRepository.java` | 修改 - 添加物理删除接口 |
| `CartItemRepositoryImpl.java` | 修改 - 实现物理删除 |
| `CartItemMapper.java` | 修改 - 添加物理删除SQL |
| `CartDomainService.java` | 修改 - 使用物理删除 |
