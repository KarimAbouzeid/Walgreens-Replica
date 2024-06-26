package com.example.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.Cache.SessionCache;
import com.example.Final.*;
import com.example.Kafka.KafkaProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;

@Service
public class AddToSavedForLaterCommand implements Command {
    @Autowired
    private JwtDecoderService jwtDecoderService;
    
    private CartRepo cartRepo;
    
    private KafkaProducer kafkaProducer;

    @Autowired
	private SessionCache sessionCache;

    private ReplyingKafkaTemplate<String, Message<String>, Message<String>> replyingKafkaTemplate;
    
    @Autowired
    public AddToSavedForLaterCommand(CartRepo cartRepo, JwtDecoderService jwtDecoderService, PromoRepo promoRepo, UserUsedPromoRepo userUsedPromoRepo,KafkaProducer kafkaProducer, SessionCache sessionCache,ReplyingKafkaTemplate<String, Message<String>, Message<String>> replyingKafkaTemplate) {
    	this.cartRepo=cartRepo;
    	this.jwtDecoderService=jwtDecoderService;
        this.kafkaProducer = kafkaProducer;
        this.sessionCache = sessionCache;

    }

    @Override
    public Object execute(Map<String, Object> data) {
        String itemId=(String)data.get("itemId");
        String userId=(String)data.get("userId");
        if(userId==null)
            return "User not found or Invalid Token";

        CartTable oldCart=cartRepo.getCart(UUID.fromString(userId));
        List<CartItem> oldItems=oldCart.getItems();
        List<CartItem> newSaved=oldCart.getSavedForLaterItems();
        if(newSaved==null) {
            newSaved=new ArrayList<CartItem>();
        }
        UUID cartId=oldCart.getId();
        boolean found=false;
        double newTotal=0;
        for(int i=0;oldItems !=null && i<oldItems.size();i++) {
            if(oldItems.get(i).getItemId().equals(UUID.fromString(itemId))) {
                newSaved.add(oldItems.get(i));
                int oldcount=oldItems.get(i).getItemCount();
                double increase=(0-oldcount)*oldItems.get(i).getPurchasedPrice();
                if(oldCart.getAppliedPromoCodeId() !=null) {
                    increase=increase - increase*oldCart.getPromoCodeAmount()/100.0;
                }
                newTotal=oldCart.getTotalAmount()+increase;
                oldItems.remove(i);
                found=true;
            }
        }
        if(found)
            cartRepo.updateCartItemsAndSaved(oldItems,newSaved, cartId,newTotal);
        else {
            return "invalid item id";
        }
        
        return "successfully added to saved for later DB";
        
    }


}
