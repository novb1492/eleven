package com.www.eleven.Test;

import com.www.eleven.Jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final JwtService jwtService;
    private final RedisTemplate<String,Object> redisTemplate;

    @RequestMapping(value = "/redis/{key}/{key2}",method = RequestMethod.GET)
    public ResponseEntity<?>redisok(@PathVariable String key,@PathVariable String key2){
        return ResponseEntity.ok().body(redisTemplate.opsForHash().get(key, key2));
    }
    @RequestMapping(value = "/redis/{key}/{key2}/{value}",method = RequestMethod.POST)
    public ResponseEntity<?>redisSave(@PathVariable String key,@PathVariable String key2,@PathVariable String value){
        redisTemplate.opsForHash().put(key,key2,jwtService.getAccessToken(value));
        return ResponseEntity.ok().body(value);
    }
    @RequestMapping(value = "/redis/{key}/{key2}/{value}",method = RequestMethod.PUT)
    public ResponseEntity<?>redisChange(@PathVariable String key,@PathVariable String key2,@PathVariable String value){
        redisTemplate.opsForHash().put(key,key2,value);
        return ResponseEntity.ok().body(value);
    }
    @RequestMapping(value = "/redis")
    public ResponseEntity<?>cookie(HttpServletResponse response){
        String token = jwtService.getAccessToken("1");
        redisTemplate.opsForHash().put("login","token",token);
        ResponseCookie cookie = ResponseCookie.from("AuthenticationText", token)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok().body(token);
    }
    @RequestMapping(value = "/api/auth/kakao/{code}/login")
    public ResponseEntity<?>kLogin(@PathVariable String code){
        System.out.println(code);
        HttpHeaders headers = new HttpHeaders(); //토큰얻어오기
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> multiValueBody = new LinkedMultiValueMap<>();
        multiValueBody.add("grant_type", "authorization_code"); //카카오에서  요청하는 고정값
        multiValueBody.add("client_id", "e73cb23af9a699bcfdb7fa44416e248d");
        multiValueBody.add("redirect_uri", "http://localhost:3000/login");
        multiValueBody.add("code",code);
        HttpEntity<MultiValueMap>entity=new HttpEntity<>(multiValueBody,headers);
        RestTemplate restTemplate=new RestTemplate();
        JSONObject response= restTemplate.postForObject("https://kauth.kakao.com/oauth/token", entity, JSONObject.class);
        System.out.println(response);
        HttpHeaders userInfoHttpHeaders= new HttpHeaders();
        userInfoHttpHeaders.add("Authorization", "Bearer "+response.get("access_token"));
        RestTemplate restTemplate2=new RestTemplate();
        HttpEntity<MultiValueMap>entity2=new HttpEntity<>(null,userInfoHttpHeaders);
        ResponseEntity<JSONObject>responseEntity=restTemplate2.exchange("https://kapi.kakao.com/v2/user/me",HttpMethod.GET,entity2,JSONObject.class);
        System.out.println(responseEntity.getBody());
        return ResponseEntity.ok().body(null);
    }

    @RequestMapping(value = "/api/auth/{start}/{end}/reservation/{page}/list")
    public ResponseEntity<?>getResevations(@PathVariable String start,@PathVariable String end,@PathVariable Integer page){
        System.out.println(start+"/"+end+"/"+page);
        JSONObject response = new JSONObject();
        List<JSONObject> reservations = new ArrayList<>();
        JSONObject reservation1 = new JSONObject();
        JSONObject reservation2 = new JSONObject();
        List<Integer> times1 = new ArrayList<>();
        List<Integer> times2 = new ArrayList<>();
        reservation1.put("reservationDate", LocalDate.now());
        reservation1.put("insertDate", LocalDateTime.now());
        reservation2.put("reservationDate", LocalDate.now());
        reservation2.put("insertDate", LocalDateTime.now());

        if(page==1){
            reservation1.put("reservationId", 1);
            times1.add(1);
            times1.add(2);
            reservation1.put("times", times1);
            reservation1.put("seat", "a-1");
            reservations.add(reservation1);
            reservation2.put("reservationId", 2);
            times2.add(3);
            times2.add(4);
            reservation2.put("times", times2);
            reservation2.put("seat", "a-2");
            reservations.add(reservation2);
            response.put("first",true);
            response.put("last",false);
        }else{
            reservation1.put("reservationId", 3);
            times1.add(5);
            times1.add(6);
            reservation1.put("times", times1);
            reservation1.put("seat", "a-3");
            reservations.add(reservation1);
            reservation2.put("reservationId", 4);
            times2.add(7);
            times2.add(8);
            reservation2.put("times", times2);
            reservation2.put("seat", "a-4");
            reservations.add(reservation2);
            response.put("first",false);
            response.put("last",true);
        }
        response.put("number",page-1);
        response.put("content",reservations);
        response.put("totalPages",2);
        return ResponseEntity.ok().body(response);
    }

//    @RequestMapping(value = "/api/auth/time/{size}")
    public ResponseEntity<?>plusTime(@PathVariable Integer size){
        System.out.println(size);
        JSONObject response = new JSONObject();
        size+=2;
        int totalPrice=10000;
        if(size==1){
            totalPrice=10000;
        }else if(size==2){
            totalPrice=20000;
        }else{
            totalPrice=40000;
        }
        response.put("minPrice",totalPrice);
        return ResponseEntity.ok().body(response);
    }
    @RequestMapping(value = "/api/auth/{reservationId}/time")
    public ResponseEntity<?>plusTime(@RequestBody JSONObject jsonObject,@PathVariable Long reservationId){
        System.out.println(jsonObject);
        return ResponseEntity.ok().body(null);
    }
    @RequestMapping(value = "/api/auth/{reservationId}/product")
    public ResponseEntity<?>plusProduct(@PathVariable Long reservationId,@RequestBody JSONObject jsonObject){
        JSONObject response = new JSONObject();
        int totalPrice=0;
        List<JSONObject> products = getProducts();
        List<LinkedHashMap<String,Object>> choiceProducts = (List<LinkedHashMap<String,Object>>) jsonObject.get("choiceProducts");
        for(LinkedHashMap<String,Object> cp:choiceProducts){
            for(JSONObject product:products){
                if(product.get("id").equals(cp.get("id"))){
                    totalPrice+=Integer.parseInt(product.get("price").toString().replaceAll(",",""))*Integer.parseInt(cp.get("count").toString());
                    break;
                }
            }
        }
        response.put("name", "커피와자리");
        response.put("price",totalPrice);
        response.put("paymentid",new Random().nextInt(10));
        return ResponseEntity.ok().body(response);
    }
    @RequestMapping(value = "/api/auth/{reservationId}/{kindId}/product/list")
    public ResponseEntity<?>getReservationAndProduct(@PathVariable Long reservationId,@PathVariable Integer kindId){
        JSONObject response = new JSONObject();
        response.put("totalTime",2);
        response.put("minPrice",20000);
        List<LinkedHashMap<String, Object>> choiceProducts = new ArrayList<>();
        LinkedHashMap<String, Object> product1 = new LinkedHashMap<>();
        LinkedHashMap<String, Object> product2 = new LinkedHashMap<>();
        product1.put("id",1);
        product1.put("count",10);
        choiceProducts.add(product1);
        product2.put("id",2);
        product2.put("count",2);
        choiceProducts.add(product2);
        List<JSONObject> products = getProducts();
        int totalPrice=0;
        for(LinkedHashMap<String,Object> cp:choiceProducts){
            for(JSONObject product:products){
                if(product.get("id").equals(cp.get("id"))){
                    int price = Integer.parseInt(product.get("price").toString().replaceAll(",", "")) * Integer.parseInt(cp.get("count").toString());
                    totalPrice+=price;
                    break;
                }
            }
        }
        response.put("products",getProducts(kindId));
        response.put("totalPrice",totalPrice);
        return ResponseEntity.ok().body(response);
    }
    @RequestMapping(value = "/api/auth/{reservationId}/reservation")
    public ResponseEntity<?>getProducts(@PathVariable Long reservationId){
        JSONObject response = new JSONObject();
        List<JSONObject> reservations = new ArrayList<>();
        List<JSONObject> times = new ArrayList<>();
        List<JSONObject> responseProducts = new ArrayList<>();
            JSONObject time = new JSONObject();
            time.put("time","2");
            time.put("cancel",false);
            times.add(time);
            JSONObject time2 = new JSONObject();
            time2.put("time","20");
            time2.put("cancel",true);
            times.add(time2);
        JSONObject seat = new JSONObject();
        seat.put("seatId",1);
        seat.put("seatName","s-1");
        response.put("seat",seat);
            response.put("times",times);
            response.put("products",reservations);
        List<LinkedHashMap<String, Object>> choiceProducts = new ArrayList<>();
        List<JSONObject> products = getProducts();
        LinkedHashMap<String, Object> product1 = new LinkedHashMap<>();
        LinkedHashMap<String, Object> product2 = new LinkedHashMap<>();
        product1.put("id",1);
        product1.put("count",10);
        choiceProducts.add(product1);
        product2.put("id",2);
        product2.put("count",2);
        choiceProducts.add(product2);
        int totalPrice=0;
        for(LinkedHashMap<String,Object> cp:choiceProducts){
            for(JSONObject product:products){
                if(product.get("id").equals(cp.get("id"))){
                    Random random = new Random();
                    product.put("paymentId",random.nextInt());
                    int price = Integer.parseInt(product.get("price").toString().replaceAll(",", "")) * Integer.parseInt(cp.get("count").toString());
                    if(totalPrice==0){
                        product.put("refund",false);
                        product.put("count",10);
                    }else{
                        product.put("refund",true);
                        product.put("count",2);
                    }
                    product.put("price",price);
                    responseProducts.add(product);
                    totalPrice+=price;
                    break;
                }
            }
        }
        response.put("totalTime",2);
        response.put("minPrice",20000);
        response.put("totalPrice",totalPrice);
        response.put("products",responseProducts);
        response.put("refund",true);
        return ResponseEntity.ok().body(response);
    }
    @RequestMapping(value = "/api/auth/save/reservation")
    public ResponseEntity<?>getProducts(@RequestBody JSONObject jsonObject){
        JSONObject response = new JSONObject();
        List<JSONObject> products = getProducts();
        int totalPrice=0;
        List<LinkedHashMap<String,Object>> choiceProducts = (List<LinkedHashMap<String,Object>>) jsonObject.get("choiceProducts");
        for(LinkedHashMap<String,Object> cp:choiceProducts){
            for(JSONObject product:products){
                if(product.get("id").equals(cp.get("id"))){
                    totalPrice+=Integer.parseInt(product.get("price").toString().replaceAll(",",""))*Integer.parseInt(cp.get("count").toString());
                    break;
                }
            }
        }
        List<Integer>times= (List<Integer>) jsonObject.get("choiceTimes");
        int size=times.size();
        if(size==1){
            if(totalPrice<=10000){
                response.put("message","한시간은 최소 주문 1만원이상입니다");
                return ResponseEntity.status(400).body(response);
            }
        }else if(size==2){
            if(totalPrice<=15000){
                response.put("message","두시간은 최소 주문 2만원 이상입니다");
                return ResponseEntity.status(400).body(response);
            }
        }else{
            if(totalPrice<=40000){
                response.put("message","세시간이상 최소 주문 4만원 이상입니다");
                return ResponseEntity.status(400).body(response);
            }
        }
        response.put("name", "커피와자리");
        response.put("price",totalPrice);
        response.put("paymentid",new Random().nextInt(10));
        return ResponseEntity.ok().body(response);
    }
//    @RequestMapping(value = "/api/auth/product/{kindId}/list/{seatId}/time")
    public ResponseEntity<?>getProducts(@PathVariable Long seatId,@PathVariable Integer kindId,HttpServletResponse httpServletResponse){
        String token = jwtService.getAccessToken("1");
        redisTemplate.opsForHash().put("login","token",token);
        ResponseCookie cookie = ResponseCookie.from("AuthenticationText", token)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        httpServletResponse.setHeader("Set-Cookie", cookie.toString());
        JSONObject response = new JSONObject();
        response.put("products",getProducts(kindId));
        response.put("times",getTimes());
        return ResponseEntity.ok().body(response);
    }
    @RequestMapping(value = "/api/auth/{reservationId}/time/list")
    public ResponseEntity<?> getTimesBySeat(@PathVariable Long reservationId) throws IOException {
        /**
         * 예약 번호로 예약 좌석 가져와서 해당 좌석으로 시간표를 던져줘야함
         */
        JSONObject response = new JSONObject();
        response.put("totalTime",2);
        response.put("minPrice",20000);
        List<LinkedHashMap<String, Object>> choiceProducts = new ArrayList<>();
        LinkedHashMap<String, Object> product1 = new LinkedHashMap<>();
        LinkedHashMap<String, Object> product2 = new LinkedHashMap<>();
        product1.put("id",1);
        product1.put("count",10);
        choiceProducts.add(product1);
        product2.put("id",2);
        product2.put("count",2);
        choiceProducts.add(product2);
        List<JSONObject> products = getProducts();
        int totalPrice=0;
        for(LinkedHashMap<String,Object> cp:choiceProducts){
            for(JSONObject product:products){
                if(product.get("id").equals(cp.get("id"))){
                    int price = Integer.parseInt(product.get("price").toString().replaceAll(",", "")) * Integer.parseInt(cp.get("count").toString());
                    totalPrice+=price;
                    break;
                }
            }
        }
        response.put("totalPrice",totalPrice);
        response.put("times",getTimes());
        return ResponseEntity.ok().body(response);
    }
    @RequestMapping(value = "/api/auth/payment")
    public void paymentResultPage(HttpServletRequest request,HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:3000/1234/result");
    }
    private List<JSONObject>getTimes(){
        List<JSONObject> times = new ArrayList<>();
        for(int i=1;i<= 23;i++){
            JSONObject time = new JSONObject();
            if(i==22|| LocalDateTime.now().getHour()>=i){
                time.put("can",false);
            }else{
                time.put("can",true);
            }
            time.put("time",i);
            times.add(time);
        }
        return times;
    }
    private List<JSONObject>getProducts(int kindId){
        List<JSONObject> products = new ArrayList<>();
        if(kindId==1){
            JSONObject product = new JSONObject();
            product.put("name","아이스 아메리카노");
            product.put("id",1);
            product.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
            product.put("soldOut",false);
            product.put("price","100");
            products.add(product);
            JSONObject product2 = new JSONObject();
            product2.put("name","바닐라 라떼");
            product2.put("id",2);
            product2.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe2.jpg");
            product2.put("soldOut",false);
            product2.put("price","20,000");
            products.add(product2);
            JSONObject product3 = new JSONObject();
            product3.put("name","복숭아 아이스티");
            product3.put("id",3);
            product3.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe2.jpg");
            product3.put("soldOut",true);
            product3.put("price","30,000");
            products.add(product3);
            JSONObject product4 = new JSONObject();
            product4.put("name","아이스 아메리카노4");
            product4.put("id",4);
            product4.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
            product4.put("soldOut",false);
            product4.put("price","10,000");
            products.add(product4);
            JSONObject product5 = new JSONObject();
            product5.put("name","아이스 아메리카노5");
            product5.put("id",5);
            product5.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
            product5.put("soldOut",false);
            product5.put("price","10,000");
            products.add(product5);
            JSONObject product6 = new JSONObject();
            product6.put("name","아이스 아메리카노6");
            product6.put("id",6);
            product6.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
            product6.put("soldOut",false);
            product6.put("price","10,000");
            products.add(product6);
            JSONObject product7 = new JSONObject();
            product7.put("name","아이스 아메리카노7");
            product7.put("id",7);
            product7.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
            product7.put("soldOut",false);
            product7.put("price","10,000");
            products.add(product7);
        }else if(kindId==2){
            JSONObject product = new JSONObject();
            product.put("name","coffe3");
            product.put("id",8);
            product.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
            product.put("soldOut",false);
            product.put("price","30,000");
            products.add(product);
            JSONObject product2 = new JSONObject();
            product2.put("name","coffe4");
            product2.put("id",9);
            product2.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe2.jpg");
            product2.put("soldOut",false);
            product2.put("price","40,000");

            products.add(product2);
        }else{

        }
        return products;
    }
    public List<JSONObject>getProducts(){
        List<JSONObject>products=new ArrayList<>();
        JSONObject product = new JSONObject();
        product.put("name","아이스 아메리카노");
        product.put("id",1);
        product.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
        product.put("soldOut",false);
        product.put("price","100");
        products.add(product);
        JSONObject product2 = new JSONObject();
        product2.put("name","바닐라 라떼");
        product2.put("id",2);
        product2.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe2.jpg");
        product2.put("soldOut",false);
        product2.put("price","20,000");
        products.add(product2);
        JSONObject product3 = new JSONObject();
        product3.put("name","복숭아 아이스티");
        product3.put("id",3);
        product3.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe2.jpg");
        product3.put("soldOut",true);
        product3.put("price","30,000");
        products.add(product3);
        JSONObject product4 = new JSONObject();
        product4.put("name","아이스 아메리카노4");
        product4.put("id",4);
        product4.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
        product4.put("soldOut",false);
        product4.put("price","10,000");
        products.add(product4);
        JSONObject product5 = new JSONObject();
        product5.put("name","아이스 아메리카노5");
        product5.put("id",5);
        product5.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
        product5.put("soldOut",false);
        product5.put("price","10,000");
        products.add(product5);
        JSONObject product6 = new JSONObject();
        product6.put("name","아이스 아메리카노6");
        product6.put("id",6);
        product6.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
        product6.put("soldOut",false);
        product6.put("price","10,000");
        products.add(product6);
        JSONObject product7 = new JSONObject();
        product7.put("name","아이스 아메리카노7");
        product7.put("id",7);
        product7.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
        product7.put("soldOut",false);
        product7.put("price","10,000");
        products.add(product7);
        JSONObject product8 = new JSONObject();
        product8.put("name","coffe3");
        product8.put("id",8);
        product8.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe1.jpg");
        product8.put("soldOut",false);
        product8.put("price","30,000");
        products.add(product8);
        JSONObject product9 = new JSONObject();
        product9.put("name","coffe4");
        product9.put("id",9);
        product9.put("img", "https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/coffe2.jpg");
        product9.put("soldOut",false);
        product9.put("price","40,000");
        return products;
    }
//    @RequestMapping(value = "/api/auth/{floor}/seat")
    public ResponseEntity<?>getSeats(@PathVariable Integer floor){
        JSONObject response = new JSONObject();
        response.put("floor",2);
        if(floor==1){
            response.put("drawing","https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/%EC%B9%B4%ED%8E%98_%ED%8F%89%EB%A9%B4%EB%8F%84_(3)+(2).jpg");
            List<JSONObject> seats = new ArrayList<>();
            JSONObject seat = new JSONObject();
            seat.put("url","https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/%EC%B9%B4%ED%8E%98_%ED%8F%89%EB%A9%B4%EB%8F%84_(3).jpg");
            seat.put("soldOut", true);
            seat.put("people", 6);
            seat.put("id",1);
            seat.put("left", 13.8);
            seat.put("top", 21.2);
            seats.add(seat);
            JSONObject seat2 = new JSONObject();
            seat2.put("url","https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/%EC%B9%B4%ED%8E%98_%ED%8F%89%EB%A9%B4%EB%8F%84_(33).jpg");
            seat2.put("soldOut", false);
            seat2.put("people", 4);
            seat2.put("id",2);
            seat2.put("left", 23.5);
            seat2.put("top", 23.5);
            seats.add(seat2);
            JSONObject seat3 = new JSONObject();
            seat3.put("url","https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/%EC%B9%B4%ED%8E%98_%ED%8F%89%EB%A9%B4%EB%8F%84_(3)+(4).jpg");
            seat3.put("soldOut", false);
            seat3.put("people", 4);
            seat3.put("id",3);
            seat3.put("left", 28);
            seat3.put("top", 23.7);
            seats.add(seat3);
            JSONObject seat4 = new JSONObject();
            seat4.put("url","https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/%EC%B9%B4%ED%8E%98_%ED%8F%89%EB%A9%B4%EB%8F%84_(3)+(5).jpg");
            seat4.put("soldOut", false);
            seat4.put("people", 4);
            seat4.put("id",4);
            seat4.put("left", 32.4);
            seat4.put("top", 23.7);
            seats.add(seat4);
            JSONObject seat5 = new JSONObject();
            seat5.put("url","https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/%EC%B9%B4%ED%8E%98_%ED%8F%89%EB%A9%B4%EB%8F%84_(3)+(6).jpg");
            seat5.put("soldOut", false);
            seat5.put("people", 4);
            seat5.put("id",5);
            seat5.put("left",23.95);
            seat5.put("top", 18.7);
            seats.add(seat5);
            JSONObject seat6 = new JSONObject();
            seat6.put("url","https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/%EC%B9%B4%ED%8E%98_%ED%8F%89%EB%A9%B4%EB%8F%84_(3)+(7).jpg");
            seat6.put("soldOut", false);
            seat6.put("people", 4);
            seat6.put("id",6);
            seat6.put("left",32.3);
            seat6.put("top", 18.7);
            seats.add(seat6);
            response.put("seats",seats);
            JSONObject counter = new JSONObject();
            counter.put("url","https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/%EC%B9%B4%ED%8E%98_%ED%8F%89%EB%A9%B4%EB%8F%84_(3)+(f).jpg");
            counter.put("id",7);
            counter.put("left",21.6);
            counter.put("top", 6.8);
            response.put("counter", counter);
        }else{
            response.put("drawing","https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/%EC%B9%B4%ED%8E%98_%ED%8F%89%EB%A9%B4%EB%8F%84_(5).jpg");
            List<JSONObject> seats = new ArrayList<>();
//            JSONObject seat = new JSONObject();
//            seat.put("url","https://eleven-fifty.s3.ap-northeast-2.amazonaws.com/%EC%B9%B4%ED%8E%98_%ED%8F%89%EB%A9%B4%EB%8F%84_(3).jpg");
//            seat.put("soldOut", false);
//            seat.put("people", 6);
//            seat.put("id",3);
//            seat.put("left", 13.8);
//            seat.put("top", 21.2);
//            seats.add(seat);
            response.put("seats",seats);
        }
        return ResponseEntity.ok().body(response);
    }

}
