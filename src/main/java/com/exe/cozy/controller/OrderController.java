package com.exe.cozy.controller;

import com.exe.cozy.domain.*;

import com.exe.cozy.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Controller
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private DeliveryService deliveryService;

    @Resource
    private ItemDetailService itemDetailService;

    @Resource
    private CustomerService customerService;
    @Resource
    private CartService cartService;
    @Resource
    private ObjectMapper objectMapper = new ObjectMapper();
    @GetMapping("/order")//order 가면 일단 리스트도 다 떠야함...
    public ModelAndView order(HttpServletRequest request, @ModelAttribute DeliverDto ddto,
                              @ModelAttribute OrderDto odto)  throws Exception {
        /*상세페이지 완성되면 이거 풀기*/
       int itemNum = Integer.parseInt(request.getParameter("num"));
      //  int itemNum =3;
        ItemDetailDto idto = itemDetailService.getReadItemData(itemNum);


        //바로결제 진행시 수량
       int itemQty = Integer.parseInt((request.getParameter("itemQty")));
     //   int itemQty = 2;
        String customerEmail="eunji";


        CustomerDto cdto = customerService.getReadData(customerEmail);
        List<DeliverDto> dlist =deliveryService.listDeliver(customerEmail);

        int saleTotalPrice =idto.getItemPrice()-idto.getItemDiscount();
        int preTotalPrice = idto.getItemPrice() *itemQty;
        int salePrice = idto.getItemDiscount() *itemQty;
        int totalPrice = preTotalPrice - salePrice;




        ModelAndView mav = new ModelAndView();
        mav.addObject("dlist",dlist);
        mav.addObject("ddto",ddto);
        mav.addObject("idto",idto);
        mav.addObject("itemNum",itemNum);
        mav.addObject("itemQty",itemQty);
        mav.addObject("saleTotalPrice",saleTotalPrice);
        mav.addObject("preTotalPrice", preTotalPrice);
        mav.addObject("salePrice", salePrice);
        mav.addObject("totalPrice",totalPrice);
        mav.addObject("itemQty",itemQty);
        mav.addObject("cdto",cdto);

        mav.setViewName("checkout");
        return mav;}

    @PostMapping("/deliver")
    public ModelAndView deliver(HttpSession session, @ModelAttribute OrderDto odto, @ModelAttribute DeliverDto ddto, HttpServletRequest request, HttpServletResponse response){
        ModelAndView mav = new ModelAndView();
        /*   int orderMaxNum = orderService.OrderMaxNum();*/
        //String customerEmail = (String)session.getAttribute("customerEmail");
        /*   odto.setOrderNum(orderMaxNum +1);
        orderService.insertOrder(odto);*/
        String itemNum = request.getParameter("itemNum");
        String itemQty = request.getParameter("itemQty");

        String customerEmail = "eunjis";
        String param = "num="+ itemNum +"&itemQty="+itemQty;
        ddto.setCustomerEmail(customerEmail);

        int deliverMaxNum = deliveryService.maxNumDeliver();
        response.setContentType("text/html; charset=UTF-8");
        ddto.setDeliverNum(deliverMaxNum+1); //배송지 번호 증가
        ddto.setDeliverType("추가");
        deliveryService.insertDeliver(ddto);


        mav.setViewName("redirect:order?"+param);

        return mav;
    }
    @PostMapping(value = "/order_ok")
    @ResponseBody
    public ModelAndView order_ok(
            HttpSession session, @ModelAttribute DeliverDto ddto,
            HttpServletRequest request) throws IOException {
        ModelAndView mav = new ModelAndView();
//제이슨으로 받은 데이터 바꾸기
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        OrderDto odto = objectMapper.readValue(messageBody, OrderDto.class);
        // 세션 String customerEmail = (String)session.getAttribute("customerEmail");
        String customerEmail = "eunjis";
       /* response.setContentType("text/html; charset=UTF-8");*/


        odto.setCustomerEmail(customerEmail);
        odto.setCartNum(1);
        odto.setDeliverNum(0);
        orderService.insertOrder(odto);


        mav.setViewName("redirect:success_order");

        return mav;
    }



    @RequestMapping("/cartOrder")//order 가면 일단 리스트도 다 떠야함...
    public ModelAndView cartOrder(HttpServletRequest request, @ModelAttribute DeliverDto ddto,
                                  @ModelAttribute OrderDto odto)  throws Exception {
        /*상세페이지 완성되면 이거 풀기*/
//        int itemNum = Integer.parseInt(request.getParameter("itemNum"));
        int itemNum =3;
        ItemDetailDto idto = itemDetailService.getReadItemData(itemNum);


        //바로결제 진행시 수량
        //int itemQty = Integer.parseInt((request.getParameter("itemQty")));
        int itemQty = 100;




        String customerEmail="eunjis";


        CustomerDto cdto = customerService.getReadData(customerEmail);
        List<DeliverDto> dlist =deliveryService.listDeliver(customerEmail);

        int saleTotalPrice =idto.getItemPrice() - idto.getItemDiscount();
        int preTotalPrice = idto.getItemPrice() *itemQty;
        int salePrice = idto.getItemDiscount() *itemQty;
        int totalPrice = preTotalPrice - saleTotalPrice;




        ModelAndView mav = new ModelAndView();
        mav.addObject("dlist",dlist);
        mav.addObject("ddto",ddto);
        mav.addObject("idto",idto);
        mav.addObject("saleTotalPrice",saleTotalPrice);
        mav.addObject("preTotalPrice", preTotalPrice);
        mav.addObject("salePrice", salePrice);
        mav.addObject("totalPrice",totalPrice);
        mav.addObject("itemQty",itemQty);
        mav.addObject("cdto",cdto);

        mav.setViewName("cartOrder");
        return mav;}

    @RequestMapping("/success_order")
    public String order_success(){ return "order-success";}


}