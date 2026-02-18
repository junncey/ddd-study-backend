package com.example.ddd.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Address;
import com.example.ddd.domain.model.entity.CartItem;
import com.example.ddd.domain.model.entity.Order;
import com.example.ddd.domain.model.entity.OrderItem;
import com.example.ddd.domain.model.entity.ProductSku;
import com.example.ddd.domain.model.valueobject.Money;
import com.example.ddd.domain.repository.AddressRepository;
import com.example.ddd.domain.repository.CartItemRepository;
import com.example.ddd.domain.repository.OrderItemRepository;
import com.example.ddd.domain.repository.OrderRepository;
import com.example.ddd.domain.repository.ProductSkuRepository;
import com.example.ddd.domain.service.OrderDomainService;
import com.example.ddd.interfaces.rest.dto.OrderCreateRequest;
import com.example.ddd.interfaces.rest.vo.OrderDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单应用服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationService extends ApplicationService {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductSkuRepository productSkuRepository;

    /**
     * 从请求创建订单
     */
    public Order createOrderFromRequest(Long userId, OrderCreateRequest request) {
        beforeExecute();
        try {
            // 获取收货地址
            Address address = addressRepository.findById(request.getAddressId());
            if (address == null) {
                throw new RuntimeException("收货地址不存在");
            }

            // 获取购物车项
            List<CartItem> cartItems = new ArrayList<>();
            for (Long cartItemId : request.getCartItemIds()) {
                CartItem item = cartItemRepository.findById(cartItemId);
                if (item != null) {
                    cartItems.add(item);
                }
            }

            if (cartItems.isEmpty()) {
                throw new RuntimeException("购物车为空");
            }

            // 创建订单
            Order order = new Order();
            order.setUserId(userId);
            order.setShopId(1L); // 默认店铺
            order.setReceiverName(address.getReceiverName());
            order.setReceiverPhone(address.getReceiverPhone());
            order.setReceiverAddress(address.getFullAddress());
            order.setRemark(request.getRemark());

            // 创建订单项
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                // 从SKU获取商品信息
                ProductSku sku = productSkuRepository.findById(cartItem.getSkuId());
                if (sku == null) {
                    throw new RuntimeException("SKU不存在: " + cartItem.getSkuId());
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(sku.getProductId());
                orderItem.setSkuId(cartItem.getSkuId());
                orderItem.setSkuName(sku.getSkuName() != null ? sku.getSkuName() : "默认规格");
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPriceSnapshot());
                orderItem.calculateTotalAmount();
                orderItems.add(orderItem);
            }

            // 计算订单总金额
            Money totalAmount = Money.zero();
            for (CartItem item : cartItems) {
                Money price = item.getPriceSnapshot();
                if (price != null) {
                    totalAmount = totalAmount.add(price.multiply(item.getQuantity()));
                }
            }
            order.setTotalAmount(totalAmount);

            return orderDomainService.createOrder(order, orderItems, request.getCartItemIds());
        } finally {
            afterExecute();
        }
    }

    /**
     * 创建订单
     */
    public Order createOrder(Order order, List<OrderItem> items, List<Long> cartItemIds) {
        beforeExecute();
        try {
            return orderDomainService.createOrder(order, items, cartItemIds);
        } finally {
            afterExecute();
        }
    }

    /**
     * 取消订单
     */
    public Order cancelOrder(Long orderId, Long userId) {
        beforeExecute();
        try {
            return orderDomainService.cancelOrder(orderId, userId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 支付订单
     */
    public Order payOrder(Long orderId) {
        beforeExecute();
        try {
            return orderDomainService.payOrder(orderId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 发货
     */
    public Order shipOrder(Long orderId) {
        beforeExecute();
        try {
            return orderDomainService.shipOrder(orderId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 确认收货
     */
    public Order completeOrder(Long orderId) {
        beforeExecute();
        try {
            return orderDomainService.completeOrder(orderId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取订单详情
     */
    public Order getOrderById(Long orderId) {
        beforeExecute();
        try {
            return orderRepository.findById(orderId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取订单详情（包含订单项）
     */
    public OrderDetailVO getOrderDetailById(Long orderId) {
        beforeExecute();
        try {
            Order order = orderRepository.findById(orderId);
            if (order == null) {
                return null;
            }
            List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
            return OrderDetailVO.from(order, items);
        } finally {
            afterExecute();
        }
    }

    /**
     * 根据订单号获取订单
     */
    public Order getOrderByOrderNo(String orderNo) {
        beforeExecute();
        try {
            return orderRepository.findByOrderNo(orderNo);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取用户订单列表
     */
    public List<Order> getOrdersByUserId(Long userId) {
        beforeExecute();
        try {
            return orderRepository.findByUserId(userId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取店铺订单列表
     */
    public List<Order> getOrdersByShopId(Long shopId) {
        beforeExecute();
        try {
            return orderRepository.findByShopId(shopId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 分页查询用户订单
     */
    public IPage<Order> pageOrdersByUserId(Long current, Long size, Long userId, Integer status) {
        beforeExecute();
        try {
            return orderRepository.pageByUserId(new Page<>(current, size), userId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 分页查询店铺订单
     */
    public IPage<Order> pageOrdersByShopId(Long current, Long size, Long shopId, Integer status) {
        beforeExecute();
        try {
            return orderRepository.pageByShopId(new Page<>(current, size), shopId);
        } finally {
            afterExecute();
        }
    }
}
