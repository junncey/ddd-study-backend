# 商品详情页多图片展示修复

## 问题描述
商品详情页无法正确展示多图片，当编辑已有商品并保存时报错。

## 问题原因
在 `FileApplicationService.bindToBusiness()` 方法中，当文件已经处于 `BOUND` 状态时，再次尝试绑定会抛出异常：
```
java.lang.RuntimeException: 文件状态不正确，无法绑定: BOUND
```

这是因为：
1. 第一次保存商品时，图片文件被绑定到商品（状态变为 BOUND）
2. 再次编辑保存时，尝试再次绑定相同的图片文件，但状态检查只允许 PENDING 状态的文件绑定

## 解决方案
修改 `FileApplicationService.bindToBusiness()` 方法，增加幂等性处理：

```java
// 如果文件已经绑定到相同的业务对象，跳过绑定（幂等操作）
if (fileInfo.isBound() && fileInfo.getBizType() == bizType
        && bizId != null && bizId.equals(fileInfo.getBizId())) {
    log.info("文件已绑定到当前业务，跳过绑定: fileKey={}, bizType={}, bizId={}", fileKey, bizType, bizId);
    return;
}

// 如果文件绑定到不同的业务，报错
if (fileInfo.isBound()) {
    throw new RuntimeException("文件已绑定到其他业务，无法重复绑定: " + fileKey);
}
```

## 修改文件
- `src/main/java/com/example/ddd/application/service/FileApplicationService.java`

## 测试验证
1. 商品详情页多图片展示正常（轮播图 + 缩略图导航）
2. 管理端编辑已有商品并保存成功
3. 多图片绑定不再报错

## 测试日期
2026-02-19
