package com.battle;

import android.graphics.Bitmap;
import android.graphics.Canvas;

//云(道具)类
public class Cloud {
    private Bitmap cloudBitmap;    //云(道具)图
    private int cloudX;            //云(道具)坐标
    private int cloudY;
    private int cloudSpeed=6;      //云(道具)移动速度
    
    Cloud(Bitmap cloudBitmap,int cloudX,int cloudY){
        this.cloudBitmap=cloudBitmap;
        this.cloudX=cloudX;
        this.cloudY=cloudY;
    }
    
    //绘制云(道具)
    public void drawCloud(Canvas canvas){
        canvas.drawBitmap(cloudBitmap, cloudX, cloudY, null);
    }
    
    //移动云(道具)
    public void moveCloud(){
        cloudY+=cloudSpeed;
    }
    
    //获取云(道具)横坐标
    public int getCloudX(){
        return cloudX;
    }
    
    //获取云(道具)纵坐标
    public int getCloudY(){
        return cloudY;
    }
}
