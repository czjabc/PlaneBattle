package com.battle;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class PlaneBattleActivity extends Activity {
    /** Called when the activity is first created. */
	
	private SurfaceView battleSurfaceView;                            //surfaceview绘图容器
	private Handler handler=new Handler();                            //接收绘图线程发送的数据，更新UI
	private int refreshTime=30;                                       //游戏画面刷新时间间隔
	
	private boolean isPaused=false;                                   //游戏是否暂停
	private boolean flag=true;                                		  //游戏绘图线程主循环是否正在进行
	private boolean isFirstLoad=true;                                 //游戏是否首次加载
	
	private int score=0;                                              //玩家得分
	
	private int playerX, playerY;                                     //玩家飞机的坐标
	private int skyY=0;                                               //天空背景图的左上角的y坐标，x坐标一直为0
	private int eventX,eventY;                                        //触屏接触点的坐标
	private int dx,dy;                                                //触屏接触点的位移
	private int noticeX,noticeY;                                      //敌机群攻提示图片左上角的坐标
	private int scoreAddingX,scoreAddingY;                            //道具加分效果图片左上角的坐标
	
	private int screenWidth, screenHeight;                            //屏幕宽高
	private int skyBitmapHeight,skyBitmapWidth;                       //天空图片的宽高
	private int playerBitmapWidth, playerBitmapHeight;                //玩家飞机图片的宽高
	private int bulletBitmapWidth, bulletBitmapHeight;                //炸弹图片的宽高
	private int enemyBitmapWidth, enemyBitmapHeight;                  //小型敌机图片的宽高
	private int enemyMBitmapWidth, enemyMBitmapHeight;                //中型敌机的宽高      
	private int enemyLBitmapWidth, enemyLBitmapHeight;                //大型敌机的宽高
	private int cloudBitmapWidth,cloudBitmapHeight;                   //云(道具)图片的宽高
	private int scoreAddingBitmapWidth,scoreAddingBitmapHeight;       //道具加分效果图片的宽高
	
	private Player player=null;                                       //玩家飞机
	private ArrayList<Bullet> bulletArray=new ArrayList<Bullet>();    //数组存储所有屏幕上显示的炸弹对象
	private ArrayList<Enemy> enemyArray=new ArrayList<Enemy>();       //数组存储所有屏幕上显示的敌机对象
	private ArrayList<Cloud> cloudArray=new ArrayList<Cloud>();       //数组存储所有屏幕上显示的云(道具)对象
	
	private long createBulletTime;                                    //创建炸弹的时间
	private long createCloudTime;                                     //创建云(道具)的时间
	private long createEnemyTime,createEnemyMTime,createEnemyLTime;   //创建敌机的时间
	private long gameTimeNow;                                         //绘图线程主循环每一轮的开始时间
	
	private boolean isPlayerLocked=false;                             //玩家飞机是否被点中(在触屏事件中)
	private boolean isDoubleBullet=false;                             //双倍炸弹是否开启
	private boolean isEnemiesComing=false;                            //敌机群攻是否开启
	private boolean isNoticeShowing=false;                            //敌机群攻提示是否展示
	private boolean isScoreAdding=false;                              //道具加分是否获得
	
	private int doubleBulletCount;                                    //双倍炸弹的发射次数
	private int noticeCount;                                          //敌机群攻提示图片的刷新次数
	private int scoreAddingCount;                                     //道具加分效果图片的刷新次数
	
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //获取屏幕宽高
        DisplayMetrics dm=getResources().getDisplayMetrics();
        screenWidth=dm.widthPixels;
        screenHeight=dm.heightPixels;
        
        //获取作战主页面的surfaceview控件
        battleSurfaceView=(SurfaceView)findViewById(R.id.battle_surfaceView);
        battleSurfaceView.getHolder().addCallback(callback);
    }
    
    private Callback callback=new Callback(){

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			System.out.println("surfaceChanged");
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			System.out.println("surfaceCreated");
			new BattleThread(holder).start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			//结束线程中的循环
			System.out.println("surfaceDestroyed");
			flag=false;
		}
    };
    
    class BattleThread extends Thread{
    	
    	SurfaceHolder holder;
    	
    	public BattleThread(SurfaceHolder holder){
    		this.holder=holder;
    	}

		@Override
		public void run() {
			super.run();
			
			//加载图片
			Bitmap skyBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.sky);
			Bitmap bulletBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.bullet);
			Bitmap playerBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.player);			
			Bitmap playerBombBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.player_bomb);
			Bitmap cloudBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.cloud);
			Bitmap cloudBombBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.cloud_bomb);
			Bitmap scoreAddingBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.score_add);
			
			//创建敌机图数组
			ArrayList<Bitmap> enemyBitmapArray=new ArrayList<Bitmap>();
			enemyBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy));
			enemyBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_m));
			enemyBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_l));
			
			//创建敌机爆炸效果图数组
			ArrayList<Bitmap> enemyBombBitmapArray=new ArrayList<Bitmap>();
			enemyBombBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_bomb));
			enemyBombBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_m_bomb));
			enemyBombBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_l_bomb));
			
			//创建敌机群攻提示图数组
			ArrayList<Bitmap> noticeBitmapArray=new ArrayList<Bitmap>();
			noticeBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_coming0));
			noticeBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_coming1));
			noticeBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_coming2));
			//敌机群攻提示图的刷新序列
            int noticeSequence[]={0,0,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2};
            
            if(isFirstLoad){
				//敌机群攻提示图刷新次数
				noticeCount=noticeSequence.length;
				
				//获取图片宽高
				skyBitmapHeight=skyBitmap.getHeight();
				skyBitmapWidth=skyBitmap.getWidth();
				playerBitmapWidth=playerBitmap.getWidth();
				playerBitmapHeight=playerBitmap.getHeight();
				bulletBitmapWidth=bulletBitmap.getWidth();
				bulletBitmapHeight=bulletBitmap.getHeight();
				enemyBitmapWidth=enemyBitmapArray.get(0).getWidth();
				enemyBitmapHeight=enemyBitmapArray.get(0).getHeight();
				enemyMBitmapWidth=enemyBitmapArray.get(1).getWidth();
				enemyMBitmapHeight=enemyBitmapArray.get(1).getHeight();
				enemyLBitmapWidth=enemyBitmapArray.get(2).getWidth();
				enemyLBitmapHeight=enemyBitmapArray.get(2).getHeight();
				cloudBitmapWidth=cloudBitmap.getWidth();
				cloudBitmapHeight=cloudBitmap.getHeight();
				scoreAddingBitmapWidth=scoreAddingBitmap.getWidth();
				scoreAddingBitmapHeight=scoreAddingBitmap.getHeight();
				
				//获取玩家飞机起始坐标
				playerX=screenWidth/2-playerBitmapWidth/2;
				playerY=screenHeight-playerBitmapHeight;
				
				//获取敌机群攻提示图坐标
	            noticeX=screenWidth/2-noticeBitmapArray.get(0).getWidth()/2;
	            noticeY=screenHeight/2-noticeBitmapArray.get(0).getHeight()/2;
	         
				//创建玩家飞机对象
				player=new Player(playerBitmap);
				
				//初始化创建各类游戏元素的时间
				createBulletTime=createEnemyTime=createEnemyMTime=createEnemyLTime=
				createCloudTime=System.currentTimeMillis();
				
				//根据屏高设置画面刷新的时间间隔
				//标准模式是800屏高,40ms刷新时间间隔
				refreshTime=32000/screenHeight;
				
				//设置为非首次加载
				isFirstLoad=false;
            }
			
			//绘图主循环
			while(flag){
								
				//控制画面刷新的时间间隔
				if(System.currentTimeMillis()-gameTimeNow<refreshTime)continue;
				//Log.v("time", System.currentTimeMillis()+"");
				
				//记录当前轮开始时间
				gameTimeNow=System.currentTimeMillis();
				
				if(!isPaused){
					
					//锁定画布，开始绘图
					Canvas canvas=holder.lockCanvas();
					//绘制天空背景
					drawSky(skyBitmap,canvas);					
					//玩家飞机与云(道具)的碰撞检测
					playerHitCloud(cloudBombBitmap,canvas);
					//炸弹与敌机的碰撞检测
					bulletHitEnemy(enemyBombBitmapArray,canvas);
					//敌机与玩家飞机的碰撞检测
					if(!enemyHitPlayer(playerBombBitmap,canvas))
						//绘制玩家飞机
						drawPlayer(playerBitmap,canvas);
					//绘制炸弹
					drawBullet(bulletBitmap,canvas);
					//绘制敌机
					drawEnemy(enemyBitmapArray,canvas);
					//绘制云(道具)
					drawCloud(cloudBitmap,canvas);
					//绘制分数
					drawScore(canvas);
					//判断敌机群攻提示是否展示，是则绘制提示
					if(isNoticeShowing)drawNotice(noticeBitmapArray,noticeSequence,canvas);
					//判断道具加分是否获得，是则绘制加分效果
					if(isScoreAdding)drawScoreAdding(scoreAddingBitmap,canvas);
					//解锁画布，提交更新
					holder.unlockCanvasAndPost(canvas);
				}			
			}
		}   	   	
    }
    //绘制天空背景
    private void drawSky(Bitmap skyBitmap,Canvas canvas){
    	    	
    	//刷新背景图的纵坐标
    	skyY+=4;
    	//天空图向下移动时，屏幕上方空出的部分再用天空图来填充
    	canvas.drawBitmap(skyBitmap, 0, skyY, null);
    	canvas.drawBitmap(skyBitmap, 0, skyY-skyBitmapHeight, null);
    	if(skyY>=screenHeight)skyY-=skyBitmapHeight;
    	
    }
  
    //绘制玩家飞机
    private void drawPlayer(Bitmap playerBitmap,Canvas canvas){
    	
    	//根据触屏接触点的位移更新玩家飞机的坐标
    	playerX+=dx;
    	playerY+=dy;
    	dx=0;
    	dy=0;
    	//边缘检测，防止玩家飞机绘制出界
    	if(playerX<=0)playerX=0;
    	if(playerX>=screenWidth-playerBitmapWidth)
    		playerX=screenWidth-playerBitmapWidth;
    	if(playerY<=0)playerY=0;
    	if(playerY>=screenHeight-playerBitmapHeight)
    		playerY=screenHeight-playerBitmapHeight;
    	player.drawPlayer(canvas, playerX, playerY);
    }
    
    //绘制炸弹
    private void drawBullet(Bitmap bulletBitmap,Canvas canvas){
    	
    	//控制炸弹创建的时间间隔
    	if(gameTimeNow-createBulletTime>=400){
    		if(!isDoubleBullet){
    			//创建单个炸弹
	    		int bulletX=playerX+(playerBitmapWidth/2-bulletBitmapWidth/2);
			    int bulletY=playerY-bulletBitmapHeight;
			    Bullet bullet=new Bullet(bulletBitmap,bulletX,bulletY);
				bulletArray.add(bullet);
				//更新炸弹创建时间
    		    createBulletTime=gameTimeNow;
    		}
    		else if(doubleBulletCount>0){
    			//创建双倍炸弹
    			int bulletX1=playerX+playerBitmapWidth-bulletBitmapWidth*3/2;
    			int bulletX2=playerX+bulletBitmapWidth/2;
    			int bulletY=playerY-bulletBitmapHeight+bulletBitmapHeight/2;
    			Bullet bullet1=new Bullet(bulletBitmap,bulletX1,bulletY);
				bulletArray.add(bullet1);
				Bullet bullet2=new Bullet(bulletBitmap,bulletX2,bulletY);
				bulletArray.add(bullet2);
				doubleBulletCount--;
				//更新炸弹创建时间
    		    createBulletTime=gameTimeNow;
    		}
    		else{
    			//双倍炸弹发射次数已用完，关闭双倍炸弹
    			isDoubleBullet=false;
    		}   		
    	}
    	//遍历数组，绘制炸弹
    	for(int i=0;i<bulletArray.size();i++){
    		Bullet iBullet=bulletArray.get(i);
    		iBullet.drawBullet(canvas);
    		//更新炸弹纵坐标
    		iBullet.moveBullet();
    		//炸弹飞出屏幕，将其从数组中移除
    		if(iBullet.getBulletY()<=0)bulletArray.remove(i);
    	}
    }
    
    //绘制敌机
    private void drawEnemy(ArrayList<Bitmap> enemyBitmapArray,Canvas canvas){
    	
    	//控制敌机创建的时间间隔
    	if(gameTimeNow-createEnemyTime>=1000){
    		//创建小型敌机
    		Random r=new Random();
    		int enemyX=r.nextInt(screenWidth-enemyBitmapWidth);
    		Enemy enemy=new Enemy(enemyBitmapArray.get(0),enemyX,0,0);
    		enemyArray.add(enemy);
    		//更新敌机创建时间
    		createEnemyTime=gameTimeNow;	
    	}
    	if(gameTimeNow-createEnemyMTime>=9000){
    		//创建中型敌机
    		Random r=new Random();
    		int enemyX=r.nextInt(screenWidth-enemyMBitmapWidth);
    		Enemy enemy=new Enemy(enemyBitmapArray.get(1),enemyX,0,1);
    		enemyArray.add(enemy);
    		//更新敌机创建时间
    		createEnemyMTime=gameTimeNow;
    	}
    	if(gameTimeNow-createEnemyLTime>=29000){
    		//创建大型敌机
    		Random r=new Random();
    		int enemyX=r.nextInt(screenWidth-enemyLBitmapWidth);
    		Enemy enemy=new Enemy(enemyBitmapArray.get(2),enemyX,0,2);
    		enemyArray.add(enemy);
    		//更新敌机创建时间
    		createEnemyLTime=gameTimeNow;
    	}
    	if(isEnemiesComing){
    		//创建至多10个小型敌机(视具体屏幕宽度而定)
    		for(int i=0;i<5;i++){
        		int enemyX1=screenWidth/2-enemyBitmapWidth-5-(10+enemyBitmapWidth)*i;
        		//若创建后的敌机将有一部分不显示在屏幕上，则停止创建
        		if(enemyX1<0)break;
        		int enemyX2=screenWidth/2+5+(10+enemyBitmapWidth)*i;
        		Enemy enemy1=new Enemy(enemyBitmapArray.get(0),enemyX1,0,0);
        		Enemy enemy2=new Enemy(enemyBitmapArray.get(0),enemyX2,0,0);
        		enemyArray.add(enemy1);
        		enemyArray.add(enemy2);
    		}
    	    //创建至多6个中型敌机(视具体屏幕宽度而定)
    		for(int i=0;i<3;i++){
        		int enemyX1=screenWidth/2-enemyMBitmapWidth-10-(20+enemyMBitmapWidth)*i;
        		//若创建后的敌机将有一部分不显示在屏幕上，则停止创建
        		if(enemyX1<0)break;
        		int enemyX2=screenWidth/2+10+(20+enemyMBitmapWidth)*i;
        		Enemy enemy1=new Enemy(enemyBitmapArray.get(1),enemyX1,-enemyMBitmapHeight,1);
        		Enemy enemy2=new Enemy(enemyBitmapArray.get(1),enemyX2,-enemyMBitmapHeight,1);
        		enemyArray.add(enemy1);
        		enemyArray.add(enemy2);
    		}
    		//创建3个大型敌机
        	Enemy enemy1=new Enemy(enemyBitmapArray.get(2),10,-enemyMBitmapHeight-enemyLBitmapHeight,2);
        	Enemy enemy2=new Enemy(enemyBitmapArray.get(2),screenWidth-enemyLBitmapWidth-10,-enemyMBitmapHeight-enemyLBitmapHeight,2);
        	Enemy enemy3=new Enemy(enemyBitmapArray.get(2),screenWidth/2-enemyLBitmapWidth/2,-enemyMBitmapHeight-enemyLBitmapHeight,2);
        	enemyArray.add(enemy1);
        	enemyArray.add(enemy2);
        	enemyArray.add(enemy3);
    		isEnemiesComing=false;
    		//更新敌机创建时间
    		createEnemyTime=createEnemyMTime=createEnemyLTime=gameTimeNow;
    	}
    	//遍历数组，绘制敌机
    	for(int i=0;i<enemyArray.size();i++){
    		Enemy iEnemy=enemyArray.get(i);
    		iEnemy.drawEnemy(canvas);
    		//更新敌机纵坐标
    		iEnemy.moveEnemy();
    		//敌机飞出屏幕，将其从数组中移除
    		if(iEnemy.getEnemyY()>=screenHeight)enemyArray.remove(i);
    	}
    }
 
    //绘制云(道具)
    private void drawCloud(Bitmap cloudBitmap,Canvas canvas){
    	
    	//控制云(道具)创建的时间间隔
    	if(gameTimeNow-createCloudTime>=20000){
    		//创建云(道具)
    		Random r=new Random();
    		int cloudX=r.nextInt(screenWidth-cloudBitmapWidth);
    		Cloud cloud=new Cloud(cloudBitmap,cloudX,0);
    		cloudArray.add(cloud);
    		//更新云(道具)创建时间
    		createCloudTime=gameTimeNow;
    	}
    	//遍历数组，绘制云(道具)
    	for(int i=0;i<cloudArray.size();i++){
    		Cloud iCloud=cloudArray.get(i);
    		iCloud.drawCloud(canvas);
    		//更新云(道具)纵坐标
    		iCloud.moveCloud();
    		//若云(道具)飞出屏幕，将其从数组中移除
    		if(iCloud.getCloudY()>=screenHeight)cloudArray.remove(i);
    	}
    }
    
    //绘制分数
    private void drawScore(Canvas canvas){
    	
    	//设置画笔属性
    	Paint paint=new Paint();
    	paint.setTextSize(30);
    	paint.setColor(Color.GRAY);
    	//道具加分时显示红色
    	if(isScoreAdding)paint.setColor(Color.RED);
    	paint.setFakeBoldText(true);
    	canvas.drawText("score: "+score,5,30,paint);
    }
    
    //绘制敌机群攻提示
    private void drawNotice(ArrayList<Bitmap> noticeBitmapArray,int[] noticeSequence,Canvas canvas){
    	if(noticeCount>0){
    		//根据敌机群攻提示图的刷新序列，依次绘制相应的图片
    		canvas.drawBitmap(noticeBitmapArray.get(noticeSequence[noticeSequence.length-noticeCount]), noticeX, noticeY, null);
    		System.out.println("noticeCount:"+noticeCount);
    		noticeCount--;
    	}
    	else{
    		//敌机群攻提示展示完毕，关闭提示
    		isNoticeShowing=false;
    		noticeCount=noticeSequence.length;
    	}
    }
 
    //绘制道具加分效果
    private void drawScoreAdding(Bitmap scoreAddingBitmap,Canvas canvas){
    	if(--scoreAddingCount>0){
    		canvas.drawBitmap(scoreAddingBitmap, scoreAddingX, scoreAddingY, null);
    		//更新道具加分效果图的纵坐标
    		scoreAddingY+=3;
    	}
    	else{
    		//道具加分效果展示完毕，关闭效果
    		isScoreAdding=false;
    	}
    }
    
    //玩家飞机与云(道具)的碰撞检测
    private void playerHitCloud(Bitmap cloudBombBitmap,Canvas canvas){
    	
    	//遍历云(道具)数组
    	for(int i=0;i<cloudArray.size();i++){
    		Cloud iCloud=cloudArray.get(i);
    		int iCloudX=iCloud.getCloudX();
    		int iCloudY=iCloud.getCloudY();
    		int playerCenterX=playerX+playerBitmapWidth/2;
    		int playerCenterY=playerY+playerBitmapHeight/2;
    		//若玩家飞机的中心坐标进入云(道具)图的范围里，则认定玩家飞机获得此道具
    		if(playerCenterX>=iCloudX&&playerCenterX<=iCloudX+cloudBitmapWidth&&playerCenterY>=iCloudY&&playerCenterY<=iCloudY+cloudBitmapHeight){
    			Random r=new Random();
    			switch(r.nextInt(3)){
    			case 0:
    				//获得双倍炸弹
    				isDoubleBullet=true;
    				doubleBulletCount=30;
    				break;
    			case 1:
    				//获得额外加分
    				score+=3000;
    				isScoreAdding=true;
    				scoreAddingCount=10;
    				scoreAddingX=iCloudX;
    				//若道具加分效果图出界，将其移至界内
                    if(scoreAddingX+scoreAddingBitmapWidth>screenWidth)scoreAddingX=screenWidth-scoreAddingBitmapWidth;
                    scoreAddingY=iCloudY-scoreAddingBitmapHeight;
                    if(scoreAddingY<0)scoreAddingY=iCloudY+cloudBitmapHeight;
    				break;
    			case 2:	
    				//开启敌机群攻，一大波飞机正在靠近
    				isEnemiesComing=true;
    				isNoticeShowing=true;
    				break;
    			}
    			//绘制云(道具)分解效果
    			canvas.drawBitmap(cloudBombBitmap, iCloudX, iCloudY, null);
    			//将云(道具)从数组中移除
    			cloudArray.remove(i);
    			i--;
    		}
    	}
    }
    
    //炸弹与敌机的碰撞检测
    private void bulletHitEnemy(ArrayList<Bitmap> enemyBombBitmapArray,Canvas canvas){
    	
    	//遍历炸弹数组
    	for(int i=0;i<bulletArray.size();i++){
    		Bullet iBullet=bulletArray.get(i);
    		int iBulletX=iBullet.getBulletX();
    		int iBulletY=iBullet.getBulletY();
    		//遍历敌机数组
    		for(int j=0;j<enemyArray.size();j++){
    			Enemy iEnemy=enemyArray.get(j);
    			int iEnemyX=iEnemy.getEnemyX();
    			int iEnemyY=iEnemy.getEnemyY();
    			int enemyBitmapWidth=iEnemy.getEnemyBimmapWidth();
    			int enemyBitmapHeight=iEnemy.getEnemyBitmaoHeight();
    			//若炸弹的左上角或右上角进入敌机图的范围内，则认定炸弹击中敌机
	    		if((iBulletX>=iEnemyX&&iBulletX<=iEnemyX+enemyBitmapWidth||
    				iBulletX+bulletBitmapWidth>=iEnemyX&&iBulletX+bulletBitmapWidth<=iEnemyX+enemyBitmapWidth)&&
    				iBulletY>=iEnemyY&&iBulletY<=iEnemyY+enemyBitmapHeight){
	    			//判断当前敌机的血量是否为零，是则绘制敌机爆炸效果，并将敌机从数组中移除
	    			if(iEnemy.hitEnemy()){
	    				canvas.drawBitmap(enemyBombBitmapArray.get(iEnemy.getType()), iEnemyX, iEnemyY, null);
	    				//加分
	    			    score+=iEnemy.getScore();
	    				enemyArray.remove(j);
	    			}
	    			//将击中敌机的炸弹从数组中移除
	    			bulletArray.remove(i);
	    			/*注意Arraylist删除元素后size的变化，防止访问数组出界*/
	    			i--;
	    			j--;	    			
	    			break;
	    		}	    		
	    	}
    	}
    }
    
    //敌机与玩家飞机的碰撞检测
    private boolean enemyHitPlayer(Bitmap playerBombBitmap,Canvas canvas){
    	
    	//遍历敌机数组
    	for(int i=0;i<enemyArray.size();i++){
    		Enemy iEnemy=enemyArray.get(i);
    		int iEnemyX=iEnemy.getEnemyX();
    		int iEnemyY=iEnemy.getEnemyY()-iEnemy.getEnemySpeed();
    		int enemyBitmapWidth=iEnemy.getEnemyBimmapWidth();
    		int enemyBitmapHeight=iEnemy.getEnemyBitmaoHeight();
    		int iEnemyCenterX=iEnemyX+enemyBitmapWidth/2;
    		int iEnemyCenterY=iEnemyY+enemyBitmapHeight/2;
    		int playerCenterX=playerX+playerBitmapWidth/2;
    		int playerCenterY=playerY+playerBitmapHeight/2;
    	    //用玩家飞机与敌机图的内切圆作为碰撞检测的范围，图片宽高均相等
    		double result=Math.sqrt((iEnemyCenterX-playerCenterX)*(iEnemyCenterX-playerCenterX)+(iEnemyCenterY-playerCenterY)*(iEnemyCenterY-playerCenterY));
    		if(result<playerBitmapWidth/2+enemyBitmapWidth/2){
    		/*//用玩家飞机与敌机图作为碰撞检测的范围，图的四个角的空白让碰撞检测效果不明显
    		  if((iEnemyX>=playerX&&iEnemyX<=playerX+playerBitmapWidth||
    				iEnemyX+enemyBitmapWidth>=playerX&&iEnemyX+enemyBitmapWidth<=playerX+playerBitmapWidth)&&
    				(iEnemyY>=playerY&&iEnemyY<=playerY+playerBitmapHeight||
    				iEnemyY+enemyBitmapHeight>=playerY&&iEnemyY+enemyBitmapHeight<=playerY+playerBitmapHeight)
    				){*/
    			System.out.println("stop:"+result);
    			canvas.drawBitmap(playerBombBitmap, playerX, playerY, null);
    			//暂停游戏，画布停止绘制
    			isPaused=true;
    			
    			handler.post(new Runnable(){

					@Override
					public void run() {
                        //弹出对话框
		    			new AlertDialog.Builder(PlaneBattleActivity.this).setTitle("游戏结束").setMessage("是否重新开始?")
		    			.setCancelable(false)
		    			.setPositiveButton("是", new OnClickListener(){
		
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//初始化游戏数据
								bulletArray.clear();
								enemyArray.clear();
								playerX=screenWidth/2-playerBitmapWidth/2;
								playerY=screenHeight-playerBitmapHeight;
								createBulletTime=createCloudTime=createEnemyLTime=createEnemyMTime=
									createEnemyTime=System.currentTimeMillis();
								score=0;
								//游戏重新开始
								isPaused=false;
								
							}
		    				
		    			}).setNegativeButton("退出", new OnClickListener(){
		
							@Override
							public void onClick(DialogInterface dialog, int which) {
								PlaneBattleActivity.this.finish();
							}
		    				
		    			}).setOnKeyListener(new OnKeyListener(){

							@Override
							public boolean onKey(DialogInterface dialog,
									int keyCode, KeyEvent event) {
								switch(keyCode){
								case KeyEvent.KEYCODE_BACK:
									//在游戏结束对话框下按返回键，视为选择"重新开始"游戏
									bulletArray.clear();
									enemyArray.clear();
									playerX=screenWidth/2-playerBitmapWidth/2;
									playerY=screenHeight-playerBitmapHeight;
									createBulletTime=createCloudTime=createEnemyLTime=createEnemyMTime=
										createEnemyTime=System.currentTimeMillis();
									score=0;
									isPaused=false;
									dialog.dismiss();
									
								}
								return false;
							}
		    				
		    			}).show();
					}});
    			return true;
    		}
    	}
    	return false;
    }
    
    
    //触屏事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			eventX=(int)event.getX();
			eventY=(int)event.getY();
			//若触屏接触点的坐标在玩家飞机图的范围内，则锁定玩家飞机
			/*if(eventX>=playerX&&eventX<=playerX+playerBitmapWidth&&
			eventY>=playerY&&eventY<=playerY+playerBitmapHeight){	*/		
				isPlayerLocked=true;
			//}
			break;
		case MotionEvent.ACTION_MOVE:
			//在玩家飞机锁定的情况下，随时记录触屏接触点的位移，以同步到玩家飞机的位移
			if(isPlayerLocked){
				int moveEventX=(int)event.getX();
				int moveEventY=(int)event.getY();
				dx+=moveEventX-eventX;
				dy+=moveEventY-eventY;
				eventX=moveEventX;
				eventY=moveEventY;
			}
			break;
		case MotionEvent.ACTION_UP:
			dx=0;
			dy=0;
			//解锁玩家飞机
			isPlayerLocked=false;
			break;
		}	
		return super.onTouchEvent(event);
	}
	

	@Override
	protected void onPause() {
		//按Home键退出时调用，结束绘图线程循环
		System.out.println("onPause");
		flag=false;
		super.onPause();
	}

	
	@Override
	protected void onResume() {
		//按Home键恢复程序时，开启绘图线程循环
		System.out.println("onResume");
		flag=true;
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			//在游戏作战页面按返回键，游戏暂停，并弹出对话框
			isPaused=true;
			handler.post(new Runnable(){

				@Override
				public void run() {
					new AlertDialog.Builder(PlaneBattleActivity.this).setTitle("游戏暂停").setMessage("是否继续游戏？")
					.setCancelable(false)
	    			.setPositiveButton("是", new OnClickListener(){
	
						@Override
						public void onClick(DialogInterface dialog, int which) {
							isPaused=false;	
						}
	    				
	    			}).setNegativeButton("退出", new OnClickListener(){
	
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PlaneBattleActivity.this.finish();
						}
	    				
	    			}).setOnKeyListener(new OnKeyListener(){

						@Override
						public boolean onKey(DialogInterface dialog,
								int keyCode, KeyEvent event) {
							//在游戏暂停对话框下按返回键，视为选择"继续"游戏
							switch(keyCode){
							case KeyEvent.KEYCODE_BACK:
								if(event.getAction()==KeyEvent.ACTION_DOWN){
									dialog.dismiss();
									isPaused=false;
								}
							}
							return false;
						}
	    				
	    			}).show();
				}				
			});
			return false;
		};
		return false;
		
	}
	
	

}