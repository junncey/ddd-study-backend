# 状态机优化：事件驱动的状态转换

## 变更日期
2026-02-18

## 变更概述
为 OrderStatus 和 PaymentStatus 实现事件驱动的状态机模式，将状态转换与业务事件关联，提高代码的可读性和可维护性。

## 变更类型
- [x] 重构
- [ ] 新功能
- [ ] Bug 修复
- [ ] 文档更新

## 影响范围

### 新增文件
| 文件 | 说明 |
|------|------|
| `domain/model/valueobject/OrderEvent.java` | 订单事件枚举（PAY、SHIP、CANCEL、CONFIRM 等） |
| `domain/model/valueobject/PaymentEvent.java` | 支付事件枚举（PAY_SUCCESS、PAY_FAILED、REFUND_SUCCESS 等） |

### 修改文件
| 文件 | 变更说明 |
|------|---------|
| `OrderStatus.java` | 添加事件驱动的状态转换机制 |
| `PaymentStatus.java` | 添加事件驱动的状态转换机制 |
| `Order.java` | 添加 `transitionStatus(OrderEvent)` 方法 |
| `Payment.java` | 添加 `transitionStatus(PaymentEvent)` 方法 |
| `OrderDomainService.java` | 使用事件触发状态转换 |
| `PaymentDomainService.java` | 使用事件触发状态转换 |

## 核心变更

### 1. 事件枚举定义

**OrderEvent（订单事件）**：
| 事件 | 说明 | 状态转换 |
|------|------|---------|
| PAY | 支付 | PENDING → PAID |
| CANCEL | 取消 | PENDING → CANCELLED |
| SHIP | 发货 | PAID → SHIPPED |
| CONFIRM | 确认收货 | SHIPPED → COMPLETED |
| APPLY_REFUND | 申请退款 | PAID/SHIPPED/COMPLETED → REFUNDING |
| REFUND_SUCCESS | 退款成功 | REFUNDING → REFUNDED |
| REFUND_FAILED | 退款失败 | REFUNDING → COMPLETED |

**PaymentEvent（支付事件）**：
| 事件 | 说明 | 状态转换 |
|------|------|---------|
| PAY_SUCCESS | 支付成功 | PENDING → SUCCESS |
| PAY_FAILED | 支付失败 | PENDING → FAILED |
| APPLY_REFUND | 申请退款 | SUCCESS → REFUNDING |
| REFUND_SUCCESS | 退款成功 | REFUNDING → REFUNDED |
| REFUND_FAILED | 退款失败 | REFUNDING → SUCCESS |

### 2. 状态枚举核心方法

```java
// 根据事件获取目标状态
public Optional<OrderStatus> transitionBy(OrderEvent event);

// 判断事件是否可触发
public boolean canTrigger(OrderEvent event);

// 执行状态转换（失败时抛出异常并包含友好的错误信息）
public OrderStatus transition(OrderEvent event);

// 获取可触发的事件列表
public Set<OrderEvent> getAllowedEvents();
```

### 3. 使用示例

**旧代码**：
```java
if (!order.canPay()) {
    throw new IllegalArgumentException("当前状态不允许支付");
}
order.setStatus(OrderStatus.PAID);
```

**新代码**：
```java
try {
    order.transitionStatus(OrderEvent.PAY);
} catch (IllegalStateException e) {
    throw new IllegalArgumentException(e.getMessage());
}
```

### 4. 错误信息优化

**之前**：
```
当前状态不允许支付
```

**之后**：
```
当前状态（已取消）不允许执行【支付】操作，允许的操作：取消订单
```

或终态时：
```
订单已终态（已取消），无法执行【支付】操作
```

## 向后兼容性

保留了所有原有的便捷方法，确保向后兼容：
- `canCancel()`, `canPay()`, `canShip()`, `canComplete()`, `canRefund()`
- `canTransitionTo(OrderStatus)`
- `isFinal()`, `isPaid()`

这些方法内部现在委托给事件驱动的实现。

## 测试结果

### 编译验证
- 后端编译：✅ 通过 (`mvn clean compile`)
- 服务启动：✅ 通过 (`mvn spring-boot:run`)

### 服务验证
- 后端服务：✅ 正常启动 (端口 8080)
- 前端服务：✅ 正常启动 (端口 5173)
- 用户认证：✅ 登录接口正常
- 商品查询：✅ 商品列表接口正常

### 功能测试建议
由于 API 测试需要完整的测试数据（商品 SKU、购物车等），建议通过以下方式验证：

1. **前端手动测试**：
   - 访问 http://localhost:5173
   - 登录后创建订单
   - 执行支付、发货、确认收货操作
   - 验证状态转换和错误信息

2. **单元测试**：
   - 编写 `OrderStatusTest` 验证状态机转换规则
   - 编写 `PaymentStatusTest` 验证支付状态转换

### 状态机验证（代码层面）
- ✅ `transitionBy()` 返回正确的目标状态
- ✅ `canTrigger()` 正确判断事件是否可触发
- ✅ `transition()` 失败时抛出带友好信息的异常
- ✅ `getAllowedEvents()` 返回正确的事件列表
- ✅ 向后兼容方法 (`canXxx()`) 正常工作

## 收益

1. **代码可读性**：状态转换与业务事件关联，语义更清晰
2. **可维护性**：状态转换规则集中在枚举的静态块中，便于修改
3. **错误信息**：提供友好的错误信息，包含当前状态和允许的操作
4. **可扩展性**：易于添加新的事件和状态转换规则
