package com.lijiaqi.bedrock.protect;

import android.app.Application;

import com.lijiaqi.bedrock.protect.handler.ActivityExceptionHandler;
import com.lijiaqi.bedrock.protect.zone.ActivityStartProtect;
import com.lijiaqi.bedrock.protect.zone.ChildThreadProtect;
import com.lijiaqi.bedrock.protect.zone.UIThreadProtect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiJiaqi
 * @date 2020/12/13
 * Description:
 * 确保android 平台的异常不会导致应用崩溃（除了系统异常，或连续性异常）
 */



public class AndroidPlatformProtect {

    private final List<IProtect> protectStrategyList = new ArrayList<>();

    private static volatile AndroidPlatformProtect singleton;

    public static AndroidPlatformProtect getInstance(ActivityExceptionHandler exceptionHandler){
        if(singleton == null){
            synchronized (AndroidPlatformProtect.class){
                if(singleton == null){
                    singleton = new AndroidPlatformProtect(exceptionHandler);
                }
            }
        }
        return singleton;
    }
    private AndroidPlatformProtect(ActivityExceptionHandler exceptionHandler){
        this.exceptionHandler = exceptionHandler;
    }

    ///activity 异常善后
    private ActivityExceptionHandler exceptionHandler;

    public AndroidPlatformProtect protectActivityStart(){
        protectStrategyList.add(new ActivityStartProtect(exceptionHandler));
        return singleton;
    }

    public AndroidPlatformProtect protectUIThread(){
        protectStrategyList.add(new UIThreadProtect(exceptionHandler));
        return singleton;
    }


    public AndroidPlatformProtect protectChildThread(){
        protectStrategyList.add(new ChildThreadProtect());
        return singleton;
    }


    public void init(Application app){
        if(protectStrategyList.isEmpty()){
            throw new RuntimeException("You must add protect strategy by method \"protectXXX\" before \"init()\"");
        }
        for(IProtect protect : protectStrategyList){
            protect.protect(app);
        }
    }


}
