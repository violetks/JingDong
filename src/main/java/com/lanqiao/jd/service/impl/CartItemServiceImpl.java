package com.lanqiao.jd.service.impl;

import com.lanqiao.jd.dao.CartItemMapper;
import com.lanqiao.jd.dao.ShopCartMapper;
import com.lanqiao.jd.entity.CartItem;
import com.lanqiao.jd.service.CartItemService;
import com.lanqiao.jd.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CartItemServiceImpl implements CartItemService {
    @Autowired
    CartItemMapper cartItemMapper;
    @Autowired
    ShopCartMapper shopCartMapper;

    @Override
    public Result insertCartItem(int userId, CartItem cartItem) {
        try{
            int shopCartId = shopCartMapper.selectByUserId(userId).getShopCartId();
            //判断购物车中是否有该商品
            List<CartItem> cartItemList = cartItemMapper.selectByShopCartId(shopCartId);
            int flag = -1;
            CartItem orginalCartItem = new CartItem();
            for (CartItem check:cartItemList) {
                if(check.getProductId() == cartItem.getProductId()){
                    orginalCartItem = check;
                    flag = check.getNum();
                    break;
                }
            }
            if(flag > 0){
                orginalCartItem.setNum(cartItem.getNum()+flag);
                if(cartItemMapper.updateByPrimaryKeySelective(orginalCartItem) > 0){
                    return Result.createSuccessResult();
                }else {
                    return Result.createByFailure("在增加商品数量时失败!");
                }
            }else {
                cartItem.setShopCartId(shopCartId);
                if (cartItemMapper.insertSelective(cartItem) > 0) {
                    return Result.createSuccessResult();
                } else {
                    return Result.createByFailure("插入失败！");
                }
            }
        }catch (Exception e){
            return Result.createByFailure("出现错误，联系管理员！");
        }
    }

    @Override
    public Result deleteCartItem(int userId, int productId) {
        try {
            int shopCartId = shopCartMapper.selectByUserId(userId).getShopCartId();
            CartItem cartItem = cartItemMapper.selectByProductId(shopCartId,productId);
            if(cartItemMapper.deleteByPrimaryKey(cartItem.getCartItemId()) > 0){
                return Result.createSuccessResult();
            }else {
                return Result.createByFailure("删除失败！");
            }

        }catch (Exception e){
            return Result.createByFailure("出现错误，联系管理员！");
        }
    }
}
