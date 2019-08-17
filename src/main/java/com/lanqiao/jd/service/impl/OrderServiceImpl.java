package com.lanqiao.jd.service.impl;

import com.lanqiao.jd.dao.OrderItemMapper;
import com.lanqiao.jd.dao.OrderMapper;
import com.lanqiao.jd.dao.UserMapper;
import com.lanqiao.jd.entity.Order;
import com.lanqiao.jd.entity.OrderItem;
import com.lanqiao.jd.entity.OrderVo;
import com.lanqiao.jd.entity.User;
import com.lanqiao.jd.service.OrderService;
import com.lanqiao.jd.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    UserMapper userMapper;

    //向订单和订单详情表中插入记录
    @Transactional
    @Override
    public Result insertOrder(OrderVo orderVo) {
        //向订单表中插入记录
        Order order = new Order();
        String date =  new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        order.setOrderStatus("待支付");
        order.setUserId(orderVo.getUserId());
        order.setCreatTime(date);
        order.setUserAddressId(orderVo.getUserAddressId());
        order.setTotalPrice(orderVo.getTotalPrice());
        int col = orderMapper.insertSelective(order);
        //获取orderId
        int orderId = order.getOrderId();
        if(col > 0){
            //向订单详情表中插入记录->订单详情有多条
            //orderId productId num
            for(OrderItem orderItem:orderVo.getOrderItem()){
                OrderItem insetOrderItem = new OrderItem();
                insetOrderItem.setOrderId(orderId);
                insetOrderItem.setProductId(orderItem.getProductId());
                insetOrderItem.setNum(orderItem.getNum());
                orderItemMapper.insertSelective(insetOrderItem);
            }
            return Result.createSuccessResult();
        }else{
            return Result.createByFailure("数据库错误，联系管理员");
        }
    }

    @Transactional
    @Override
    //支付->检查用户余额->减少用户余额->更改订单状态为已支付
    //need:userId orderId
    public Result pay(Order order) {
        //根据userId查询用户余额
        User user = userMapper.selectByPrimaryKey(order.getUserId());
        BigDecimal balance = user.getBalance();
        //根据orderId查询totalPrice
        Order so = orderMapper.selectByPrimaryKey(order.getOrderId());
        BigDecimal totalPrice = so.getTotalPrice();
        String status = so.getOrderStatus();
        if(status.equals("已支付")){
            return  Result.createByFailure("您已支付，请勿重复支付");
        }
        //判断余额是否足够支付
        int a = balance.compareTo(totalPrice);
        //足够支付
        if(a >=0){
            //余额减少
            balance = balance.subtract(totalPrice);
            //存入数据库中
            user.setBalance(balance);
            userMapper.updateByPrimaryKeySelective(user);
            //更改订单状态为已支付
            order.setOrderStatus("已支付");
            orderMapper.updateByPrimaryKeySelective(order);
            return Result.createSuccessResult(balance);
        }
        else{
            return Result.createByFailure("余额不足，请充值");
        }
    }
}
