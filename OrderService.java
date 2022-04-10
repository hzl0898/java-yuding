package cn.sy.service;

import cn.sy.mapper.FoodMapper;
import cn.sy.mapper.IntegralMapper;
import cn.sy.mapper.MemberMapper;
import cn.sy.mapper.OrderMapper;
import cn.sy.pojo.Food;
import cn.sy.pojo.Integral;
import cn.sy.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

import static java.lang.Math.ceil;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    private IntegralMapper integralMapper;

    public List<Order> findorderAll(){
        return orderMapper.selectAll();
    }

    public int deleteorderById(int id) {
        return orderMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public int saveorder(String fids,int mid) {
        double money=0;
        String[] split = fids.split(",");
        for (String fid : split){
            Food food = foodMapper.selectByPrimaryKey(Integer.parseInt(fid));
            money+=food.getShopprice();

        }

        Order order=new Order();
        order.setMid(mid);
        order.setCtime(new Date());
        order.setMoney(money);
        order.setStatus(0);
        order.setPid(0);
        orderMapper.insertSelective(order);

        for (String fid : split){
            orderMapper.saveFoodAndDetail(order.getId(),Integer.parseInt(fid));
        }

        Integral integral = new Integral();
        integral.setMid(mid);
        Integral integral1 = integralMapper.selectOne(integral);
        int m = new Double(money).intValue();
        if (integral1 == null) {
            integral1.setNum(m);
        }else {
            Integer newNum = integral1.getNum()+m;
            integral1.setNum(newNum);
        }

        integralMapper.updateByPrimaryKeySelective(integral1);
        return 1;
    }

    public Order findorderById(int id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    @Transactional
    public int updateorder(Order order,String fids) {
        //删除订单中的所用菜品
        int i = orderMapper.deleteOrderDetail(order.getId());
        //建立新的关系
        String[] split = fids.split(",");
        for(String fid : split){
            orderMapper.saveFoodAndDetail(order.getId(),Integer.parseInt(fid));
        }
        return orderMapper.updateByPrimaryKeySelective(order);
    }

    public List<Order> findorderAllwjs() {
        Order o=new Order();
        o.setStatus(0);
        List<Order> orders = orderMapper.select(o);
        for(Order order : orders){
            order.setMember(memberMapper.selectByPrimaryKey(order.getMid()));
            order.setFoods(foodMapper.findFoodByOid(order.getId()));
        }
        return orders;
    }

    public List<Order> findorderAllyjs() {
        Order o=new Order();
        o.setStatus(1);
        List<Order> orders = orderMapper.select(o);
        for(Order order : orders){
            order.setMember(memberMapper.selectByPrimaryKey(order.getMid()));
            order.setFoods(foodMapper.findFoodByOid(order.getId()));
        }
        return orders;
    }

    public int updateOrderStatusByOid(int oid) {
        Order o=new Order();
        o.setId(oid);
        o.setStatus(1);
        return orderMapper.updateByPrimaryKeySelective(o);
    }

    public List<Order> findorderByMid(int mid) {
        Order o=new Order();
        o.setMid(mid);
        List<Order> orders = orderMapper.select(o);
        for(Order order : orders){
            order.setMember(memberMapper.selectByPrimaryKey(order.getMid()));
            order.setFoods(foodMapper.findFoodByOid(order.getId()));
        }
        return orders;
    }
}
