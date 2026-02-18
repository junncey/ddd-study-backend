package com.example.ddd.application.service;

import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.*;
import com.example.ddd.domain.repository.*;
import com.example.ddd.infrastructure.security.SecurityUtil;
import com.example.ddd.interfaces.rest.exception.ForbiddenException;
import com.example.ddd.interfaces.rest.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 权限校验服务
 * 统一处理各类资源的权限校验
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService extends ApplicationService {

    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    // ==================== 地址权限校验 ====================

    /**
     * 验证地址归属
     *
     * @param addressId 地址ID
     * @throws NotFoundException 地址不存在
     * @throws ForbiddenException 无权访问
     */
    public void checkAddressOwnership(Long addressId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Address address = addressRepository.findById(addressId);
        if (address == null) {
            throw new NotFoundException("地址不存在");
        }
        if (!address.getUserId().equals(currentUserId)) {
            log.warn("用户 {} 尝试访问不属于自己的地址 {}", currentUserId, addressId);
            throw new ForbiddenException("无权访问该地址");
        }
    }

    /**
     * 验证地址归属并返回地址对象
     *
     * @param addressId 地址ID
     * @return 地址对象
     */
    public Address getOwnedAddress(Long addressId) {
        checkAddressOwnership(addressId);
        return addressRepository.findById(addressId);
    }

    // ==================== 订单权限校验 ====================

    /**
     * 验证订单归属（买家权限）
     *
     * @param orderId 订单ID
     * @throws NotFoundException 订单不存在
     * @throws ForbiddenException 无权访问
     */
    public void checkOrderOwnership(Long orderId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("订单不存在");
        }
        if (!order.getUserId().equals(currentUserId)) {
            log.warn("用户 {} 尝试访问不属于自己的订单 {}", currentUserId, orderId);
            throw new ForbiddenException("无权访问该订单");
        }
    }

    /**
     * 验证订单归属（买家权限）通过订单号
     *
     * @param orderNo 订单号
     */
    public void checkOrderOwnershipByOrderNo(String orderNo) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Order order = orderRepository.findByOrderNo(orderNo);
        if (order == null) {
            throw new NotFoundException("订单不存在");
        }
        if (!order.getUserId().equals(currentUserId)) {
            log.warn("用户 {} 尝试访问不属于自己的订单 {}", currentUserId, orderNo);
            throw new ForbiddenException("无权访问该订单");
        }
    }

    /**
     * 验证订单归属并返回订单对象
     *
     * @param orderId 订单ID
     * @return 订单对象
     */
    public Order getOwnedOrder(Long orderId) {
        checkOrderOwnership(orderId);
        return orderRepository.findById(orderId);
    }

    /**
     * 验证订单的商家权限（发货等操作）
     *
     * @param orderId 订单ID
     * @throws NotFoundException 订单不存在
     * @throws ForbiddenException 无权操作
     */
    public void checkOrderMerchantAccess(Long orderId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("订单不存在");
        }
        Shop shop = shopRepository.findById(order.getShopId());
        if (shop == null || !shop.getOwnerId().equals(currentUserId)) {
            log.warn("用户 {} 尝试操作不属于自己店铺的订单 {}", currentUserId, orderId);
            throw new ForbiddenException("无权操作该订单");
        }
    }

    // ==================== 购物车权限校验 ====================

    /**
     * 验证购物车项归属
     *
     * @param cartItemId 购物车项ID
     * @throws NotFoundException 购物车项不存在
     * @throws ForbiddenException 无权访问
     */
    public void checkCartItemOwnership(Long cartItemId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        CartItem cartItem = cartItemRepository.findById(cartItemId);
        if (cartItem == null) {
            throw new NotFoundException("购物车商品不存在");
        }
        Cart cart = cartRepository.findById(cartItem.getCartId());
        if (cart == null || !cart.getUserId().equals(currentUserId)) {
            log.warn("用户 {} 尝试访问不属于自己的购物车项 {}", currentUserId, cartItemId);
            throw new ForbiddenException("无权访问该购物车商品");
        }
    }

    /**
     * 验证购物车项归属并返回购物车项对象
     *
     * @param cartItemId 购物车项ID
     * @return 购物车项对象
     */
    public CartItem getOwnedCartItem(Long cartItemId) {
        checkCartItemOwnership(cartItemId);
        return cartItemRepository.findById(cartItemId);
    }

    // ==================== 店铺权限校验 ====================

    /**
     * 验证店铺归属（店主权限）
     *
     * @param shopId 店铺ID
     * @throws NotFoundException 店铺不存在
     * @throws ForbiddenException 无权访问
     */
    public void checkShopOwnership(Long shopId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Shop shop = shopRepository.findById(shopId);
        if (shop == null) {
            throw new NotFoundException("店铺不存在");
        }
        if (!shop.getOwnerId().equals(currentUserId)) {
            log.warn("用户 {} 尝试访问不属于自己的店铺 {}", currentUserId, shopId);
            throw new ForbiddenException("无权操作该店铺");
        }
    }

    /**
     * 验证店铺归属并返回店铺对象
     *
     * @param shopId 店铺ID
     * @return 店铺对象
     */
    public Shop getOwnedShop(Long shopId) {
        checkShopOwnership(shopId);
        return shopRepository.findById(shopId);
    }

    /**
     * 验证店铺审核权限（管理员权限）
     */
    public void checkShopApprovalPermission() {
        if (!SecurityUtil.isAdmin()) {
            throw new ForbiddenException("需要管理员权限才能审核店铺");
        }
    }

    // ==================== 商品权限校验 ====================

    /**
     * 验证商品归属（商家权限）
     *
     * @param productId 商品ID
     * @throws NotFoundException 商品不存在
     * @throws ForbiddenException 无权访问
     */
    public void checkProductOwnership(Long productId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new NotFoundException("商品不存在");
        }
        Shop shop = shopRepository.findById(product.getShopId());
        if (shop == null || !shop.getOwnerId().equals(currentUserId)) {
            log.warn("用户 {} 尝试访问不属于自己店铺的商品 {}", currentUserId, productId);
            throw new ForbiddenException("无权操作该商品");
        }
    }

    /**
     * 验证商品归属并返回商品对象
     *
     * @param productId 商品ID
     * @return 商品对象
     */
    public Product getOwnedProduct(Long productId) {
        checkProductOwnership(productId);
        return productRepository.findById(productId);
    }

    /**
     * 验证店铺归属（用于创建商品时验证）
     *
     * @param shopId 店铺ID
     */
    public void checkProductCreatePermission(Long shopId) {
        checkShopOwnership(shopId);
    }

    // ==================== 支付权限校验 ====================

    /**
     * 验证支付记录归属（通过订单）
     *
     * @param paymentId 支付记录ID
     */
    public void checkPaymentOwnership(Long paymentId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            throw new NotFoundException("支付记录不存在");
        }
        Order order = orderRepository.findById(payment.getOrderId());
        if (order == null || !order.getUserId().equals(currentUserId)) {
            log.warn("用户 {} 尝试访问不属于自己的支付记录 {}", currentUserId, paymentId);
            throw new ForbiddenException("无权访问该支付记录");
        }
    }

    /**
     * 验证订单的支付权限
     *
     * @param orderId 订单ID
     */
    public void checkPaymentPermission(Long orderId) {
        checkOrderOwnership(orderId);
    }

    // ==================== 管理员权限校验 ====================

    /**
     * 验证管理员权限
     *
     * @throws ForbiddenException 非管理员
     */
    public void checkAdminPermission() {
        if (!SecurityUtil.isAdmin()) {
            throw new ForbiddenException("需要管理员权限");
        }
    }
}
