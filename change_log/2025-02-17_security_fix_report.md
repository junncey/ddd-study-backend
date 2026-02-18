# 权限校验安全修复报告

**日期**: 2026-02-17
**修复类型**: 安全漏洞修复

---

## 1. 问题描述

系统存在严重的越权访问漏洞，用户可以通过遍历ID修改/查看其他用户的数据，包括：
- 收货地址
- 购物车
- 订单
- 商品
- 店铺
- 支付记录

## 2. 修复方案

采用 **SecurityUtil + AuthorizationService** 的方案：
- 用户ID从JWT Token解析，无法伪造
- 创建统一的权限校验服务
- 在控制器层调用权限校验

## 3. 新增文件

| 文件 | 说明 |
|------|------|
| `SecurityUtil.java` | 从Spring Security上下文获取当前用户信息 |
| `ForbiddenException.java` | 403权限不足异常 |
| `UnauthorizedException.java` | 401未认证异常 |
| `NotFoundException.java` | 404资源不存在异常 |
| `AuthorizationService.java` | 统一权限校验服务 |

## 4. 修改的文件

| 文件 | 修改内容 |
|------|---------|
| `GlobalExceptionHandler.java` | 添加新异常处理方法 |
| `AddressController.java` | 添加地址归属校验，移除X-User-Id参数 |
| `CartController.java` | 添加购物车项归属校验，移除X-User-Id参数 |
| `OrderController.java` | 添加订单归属/商家权限校验，移除X-User-Id参数 |
| `ProductController.java` | 添加商品归属校验，移除X-User-Id参数 |
| `ShopController.java` | 添加店主/管理员权限校验，移除X-User-Id参数 |
| `PaymentController.java` | 添加支付权限校验，移除X-User-Id参数 |
| `CategoryController.java` | 添加管理员权限校验 |
| `frontend/request.ts` | 移除X-User-Id请求头 |
| `application.yml` | 添加开发环境JWT密钥和数据库密码默认值 |

## 5. 权限校验逻辑

### 5.1 地址权限
- `checkAddressOwnership(addressId)` - 验证地址属于当前用户

### 5.2 订单权限
- `checkOrderOwnership(orderId)` - 验证订单属于当前用户（买家权限）
- `checkOrderMerchantAccess(orderId)` - 验证订单属于当前用户的店铺（商家权限）

### 5.3 购物车权限
- `checkCartItemOwnership(cartItemId)` - 验证购物车项属于当前用户

### 5.4 店铺权限
- `checkShopOwnership(shopId)` - 验证店铺属于当前用户（店主权限）
- `checkAdminPermission()` - 验证管理员权限

### 5.5 商品权限
- `checkProductOwnership(productId)` - 验证商品属于当前用户的店铺
- `checkProductCreatePermission(shopId)` - 验证可以创建商品的店铺归属

### 5.6 支付权限
- `checkPaymentOwnership(paymentId)` - 验证支付记录属于当前用户
- `checkPaymentPermission(orderId)` - 验证订单支付权限

## 6. 测试结果

### 6.1 功能测试
| 测试项 | 结果 |
|--------|------|
| 用户登录 | ✅ 通过 |
| 浏览商品列表 | ✅ 通过 |
| 查看商品详情 | ✅ 通过 |
| 添加购物车 | ✅ 通过 |
| 查看购物车 | ✅ 通过 |
| 创建订单 | ✅ 通过 |
| 查看订单详情 | ✅ 通过 |

### 6.2 权限校验测试
| 测试项 | 预期结果 | 实际结果 |
|--------|---------|---------|
| admin2访问admin的订单 | 403 Forbidden | ✅ 403 "无权访问该订单" |
| admin2访问admin的地址 | 403 Forbidden | ✅ 403 "无权访问该地址" |
| admin访问自己的订单 | 200 OK | ✅ 200 返回订单数据 |
| admin访问自己的地址 | 200 OK | ✅ 200 返回地址数据 |

## 7. 安全改进

1. **用户ID来源改变**: 从前端传递改为从JWT Token解析，防止伪造
2. **统一权限校验**: 所有敏感操作都经过权限验证
3. **异常处理完善**: 返回正确的HTTP状态码（401/403/404）

## 8. 注意事项

1. 生产环境必须设置 `JWT_SECRET` 环境变量
2. 生产环境必须配置数据库密码
3. `mockSuccess` 支付接口仅用于测试，生产环境应移除或加签名验证
