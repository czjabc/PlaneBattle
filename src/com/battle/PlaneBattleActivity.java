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
	
	private SurfaceView battleSurfaceView;                            //surfaceview��ͼ����
	private Handler handler=new Handler();                            //���ջ�ͼ�̷߳��͵����ݣ�����UI
	private int refreshTime=30;                                       //��Ϸ����ˢ��ʱ����
	
	private boolean isPaused=false;                                   //��Ϸ�Ƿ���ͣ
	private boolean flag=true;                                		  //��Ϸ��ͼ�߳���ѭ���Ƿ����ڽ���
	private boolean isFirstLoad=true;                                 //��Ϸ�Ƿ��״μ���
	
	private int score=0;                                              //��ҵ÷�
	
	private int playerX, playerY;                                     //��ҷɻ�������
	private int skyY=0;                                               //��ձ���ͼ�����Ͻǵ�y���꣬x����һֱΪ0
	private int eventX,eventY;                                        //�����Ӵ��������
	private int dx,dy;                                                //�����Ӵ����λ��
	private int noticeX,noticeY;                                      //�л�Ⱥ����ʾͼƬ���Ͻǵ�����
	private int scoreAddingX,scoreAddingY;                            //���߼ӷ�Ч��ͼƬ���Ͻǵ�����
	
	private int screenWidth, screenHeight;                            //��Ļ���
	private int skyBitmapHeight,skyBitmapWidth;                       //���ͼƬ�Ŀ��
	private int playerBitmapWidth, playerBitmapHeight;                //��ҷɻ�ͼƬ�Ŀ��
	private int bulletBitmapWidth, bulletBitmapHeight;                //ը��ͼƬ�Ŀ��
	private int enemyBitmapWidth, enemyBitmapHeight;                  //С�͵л�ͼƬ�Ŀ��
	private int enemyMBitmapWidth, enemyMBitmapHeight;                //���͵л��Ŀ��      
	private int enemyLBitmapWidth, enemyLBitmapHeight;                //���͵л��Ŀ��
	private int cloudBitmapWidth,cloudBitmapHeight;                   //��(����)ͼƬ�Ŀ��
	private int scoreAddingBitmapWidth,scoreAddingBitmapHeight;       //���߼ӷ�Ч��ͼƬ�Ŀ��
	
	private Player player=null;                                       //��ҷɻ�
	private ArrayList<Bullet> bulletArray=new ArrayList<Bullet>();    //����洢������Ļ����ʾ��ը������
	private ArrayList<Enemy> enemyArray=new ArrayList<Enemy>();       //����洢������Ļ����ʾ�ĵл�����
	private ArrayList<Cloud> cloudArray=new ArrayList<Cloud>();       //����洢������Ļ����ʾ����(����)����
	
	private long createBulletTime;                                    //����ը����ʱ��
	private long createCloudTime;                                     //������(����)��ʱ��
	private long createEnemyTime,createEnemyMTime,createEnemyLTime;   //�����л���ʱ��
	private long gameTimeNow;                                         //��ͼ�߳���ѭ��ÿһ�ֵĿ�ʼʱ��
	
	private boolean isPlayerLocked=false;                             //��ҷɻ��Ƿ񱻵���(�ڴ����¼���)
	private boolean isDoubleBullet=false;                             //˫��ը���Ƿ���
	private boolean isEnemiesComing=false;                            //�л�Ⱥ���Ƿ���
	private boolean isNoticeShowing=false;                            //�л�Ⱥ����ʾ�Ƿ�չʾ
	private boolean isScoreAdding=false;                              //���߼ӷ��Ƿ���
	
	private int doubleBulletCount;                                    //˫��ը���ķ������
	private int noticeCount;                                          //�л�Ⱥ����ʾͼƬ��ˢ�´���
	private int scoreAddingCount;                                     //���߼ӷ�Ч��ͼƬ��ˢ�´���
	
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //��ȡ��Ļ���
        DisplayMetrics dm=getResources().getDisplayMetrics();
        screenWidth=dm.widthPixels;
        screenHeight=dm.heightPixels;
        
        //��ȡ��ս��ҳ���surfaceview�ؼ�
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
			//�����߳��е�ѭ��
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
			
			//����ͼƬ
			Bitmap skyBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.sky);
			Bitmap bulletBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.bullet);
			Bitmap playerBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.player);			
			Bitmap playerBombBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.player_bomb);
			Bitmap cloudBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.cloud);
			Bitmap cloudBombBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.cloud_bomb);
			Bitmap scoreAddingBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.score_add);
			
			//�����л�ͼ����
			ArrayList<Bitmap> enemyBitmapArray=new ArrayList<Bitmap>();
			enemyBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy));
			enemyBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_m));
			enemyBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_l));
			
			//�����л���ըЧ��ͼ����
			ArrayList<Bitmap> enemyBombBitmapArray=new ArrayList<Bitmap>();
			enemyBombBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_bomb));
			enemyBombBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_m_bomb));
			enemyBombBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_l_bomb));
			
			//�����л�Ⱥ����ʾͼ����
			ArrayList<Bitmap> noticeBitmapArray=new ArrayList<Bitmap>();
			noticeBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_coming0));
			noticeBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_coming1));
			noticeBitmapArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_coming2));
			//�л�Ⱥ����ʾͼ��ˢ������
            int noticeSequence[]={0,0,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2};
            
            if(isFirstLoad){
				//�л�Ⱥ����ʾͼˢ�´���
				noticeCount=noticeSequence.length;
				
				//��ȡͼƬ���
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
				
				//��ȡ��ҷɻ���ʼ����
				playerX=screenWidth/2-playerBitmapWidth/2;
				playerY=screenHeight-playerBitmapHeight;
				
				//��ȡ�л�Ⱥ����ʾͼ����
	            noticeX=screenWidth/2-noticeBitmapArray.get(0).getWidth()/2;
	            noticeY=screenHeight/2-noticeBitmapArray.get(0).getHeight()/2;
	         
				//������ҷɻ�����
				player=new Player(playerBitmap);
				
				//��ʼ������������ϷԪ�ص�ʱ��
				createBulletTime=createEnemyTime=createEnemyMTime=createEnemyLTime=
				createCloudTime=System.currentTimeMillis();
				
				//�����������û���ˢ�µ�ʱ����
				//��׼ģʽ��800����,40msˢ��ʱ����
				refreshTime=32000/screenHeight;
				
				//����Ϊ���״μ���
				isFirstLoad=false;
            }
			
			//��ͼ��ѭ��
			while(flag){
								
				//���ƻ���ˢ�µ�ʱ����
				if(System.currentTimeMillis()-gameTimeNow<refreshTime)continue;
				//Log.v("time", System.currentTimeMillis()+"");
				
				//��¼��ǰ�ֿ�ʼʱ��
				gameTimeNow=System.currentTimeMillis();
				
				if(!isPaused){
					
					//������������ʼ��ͼ
					Canvas canvas=holder.lockCanvas();
					//������ձ���
					drawSky(skyBitmap,canvas);					
					//��ҷɻ�����(����)����ײ���
					playerHitCloud(cloudBombBitmap,canvas);
					//ը����л�����ײ���
					bulletHitEnemy(enemyBombBitmapArray,canvas);
					//�л�����ҷɻ�����ײ���
					if(!enemyHitPlayer(playerBombBitmap,canvas))
						//������ҷɻ�
						drawPlayer(playerBitmap,canvas);
					//����ը��
					drawBullet(bulletBitmap,canvas);
					//���Ƶл�
					drawEnemy(enemyBitmapArray,canvas);
					//������(����)
					drawCloud(cloudBitmap,canvas);
					//���Ʒ���
					drawScore(canvas);
					//�жϵл�Ⱥ����ʾ�Ƿ�չʾ�����������ʾ
					if(isNoticeShowing)drawNotice(noticeBitmapArray,noticeSequence,canvas);
					//�жϵ��߼ӷ��Ƿ��ã�������Ƽӷ�Ч��
					if(isScoreAdding)drawScoreAdding(scoreAddingBitmap,canvas);
					//�����������ύ����
					holder.unlockCanvasAndPost(canvas);
				}			
			}
		}   	   	
    }
    //������ձ���
    private void drawSky(Bitmap skyBitmap,Canvas canvas){
    	    	
    	//ˢ�±���ͼ��������
    	skyY+=4;
    	//���ͼ�����ƶ�ʱ����Ļ�Ϸ��ճ��Ĳ����������ͼ�����
    	canvas.drawBitmap(skyBitmap, 0, skyY, null);
    	canvas.drawBitmap(skyBitmap, 0, skyY-skyBitmapHeight, null);
    	if(skyY>=screenHeight)skyY-=skyBitmapHeight;
    	
    }
  
    //������ҷɻ�
    private void drawPlayer(Bitmap playerBitmap,Canvas canvas){
    	
    	//���ݴ����Ӵ����λ�Ƹ�����ҷɻ�������
    	playerX+=dx;
    	playerY+=dy;
    	dx=0;
    	dy=0;
    	//��Ե��⣬��ֹ��ҷɻ����Ƴ���
    	if(playerX<=0)playerX=0;
    	if(playerX>=screenWidth-playerBitmapWidth)
    		playerX=screenWidth-playerBitmapWidth;
    	if(playerY<=0)playerY=0;
    	if(playerY>=screenHeight-playerBitmapHeight)
    		playerY=screenHeight-playerBitmapHeight;
    	player.drawPlayer(canvas, playerX, playerY);
    }
    
    //����ը��
    private void drawBullet(Bitmap bulletBitmap,Canvas canvas){
    	
    	//����ը��������ʱ����
    	if(gameTimeNow-createBulletTime>=400){
    		if(!isDoubleBullet){
    			//��������ը��
	    		int bulletX=playerX+(playerBitmapWidth/2-bulletBitmapWidth/2);
			    int bulletY=playerY-bulletBitmapHeight;
			    Bullet bullet=new Bullet(bulletBitmap,bulletX,bulletY);
				bulletArray.add(bullet);
				//����ը������ʱ��
    		    createBulletTime=gameTimeNow;
    		}
    		else if(doubleBulletCount>0){
    			//����˫��ը��
    			int bulletX1=playerX+playerBitmapWidth-bulletBitmapWidth*3/2;
    			int bulletX2=playerX+bulletBitmapWidth/2;
    			int bulletY=playerY-bulletBitmapHeight+bulletBitmapHeight/2;
    			Bullet bullet1=new Bullet(bulletBitmap,bulletX1,bulletY);
				bulletArray.add(bullet1);
				Bullet bullet2=new Bullet(bulletBitmap,bulletX2,bulletY);
				bulletArray.add(bullet2);
				doubleBulletCount--;
				//����ը������ʱ��
    		    createBulletTime=gameTimeNow;
    		}
    		else{
    			//˫��ը��������������꣬�ر�˫��ը��
    			isDoubleBullet=false;
    		}   		
    	}
    	//�������飬����ը��
    	for(int i=0;i<bulletArray.size();i++){
    		Bullet iBullet=bulletArray.get(i);
    		iBullet.drawBullet(canvas);
    		//����ը��������
    		iBullet.moveBullet();
    		//ը���ɳ���Ļ��������������Ƴ�
    		if(iBullet.getBulletY()<=0)bulletArray.remove(i);
    	}
    }
    
    //���Ƶл�
    private void drawEnemy(ArrayList<Bitmap> enemyBitmapArray,Canvas canvas){
    	
    	//���Ƶл�������ʱ����
    	if(gameTimeNow-createEnemyTime>=1000){
    		//����С�͵л�
    		Random r=new Random();
    		int enemyX=r.nextInt(screenWidth-enemyBitmapWidth);
    		Enemy enemy=new Enemy(enemyBitmapArray.get(0),enemyX,0,0);
    		enemyArray.add(enemy);
    		//���µл�����ʱ��
    		createEnemyTime=gameTimeNow;	
    	}
    	if(gameTimeNow-createEnemyMTime>=9000){
    		//�������͵л�
    		Random r=new Random();
    		int enemyX=r.nextInt(screenWidth-enemyMBitmapWidth);
    		Enemy enemy=new Enemy(enemyBitmapArray.get(1),enemyX,0,1);
    		enemyArray.add(enemy);
    		//���µл�����ʱ��
    		createEnemyMTime=gameTimeNow;
    	}
    	if(gameTimeNow-createEnemyLTime>=29000){
    		//�������͵л�
    		Random r=new Random();
    		int enemyX=r.nextInt(screenWidth-enemyLBitmapWidth);
    		Enemy enemy=new Enemy(enemyBitmapArray.get(2),enemyX,0,2);
    		enemyArray.add(enemy);
    		//���µл�����ʱ��
    		createEnemyLTime=gameTimeNow;
    	}
    	if(isEnemiesComing){
    		//��������10��С�͵л�(�Ӿ�����Ļ��ȶ���)
    		for(int i=0;i<5;i++){
        		int enemyX1=screenWidth/2-enemyBitmapWidth-5-(10+enemyBitmapWidth)*i;
        		//��������ĵл�����һ���ֲ���ʾ����Ļ�ϣ���ֹͣ����
        		if(enemyX1<0)break;
        		int enemyX2=screenWidth/2+5+(10+enemyBitmapWidth)*i;
        		Enemy enemy1=new Enemy(enemyBitmapArray.get(0),enemyX1,0,0);
        		Enemy enemy2=new Enemy(enemyBitmapArray.get(0),enemyX2,0,0);
        		enemyArray.add(enemy1);
        		enemyArray.add(enemy2);
    		}
    	    //��������6�����͵л�(�Ӿ�����Ļ��ȶ���)
    		for(int i=0;i<3;i++){
        		int enemyX1=screenWidth/2-enemyMBitmapWidth-10-(20+enemyMBitmapWidth)*i;
        		//��������ĵл�����һ���ֲ���ʾ����Ļ�ϣ���ֹͣ����
        		if(enemyX1<0)break;
        		int enemyX2=screenWidth/2+10+(20+enemyMBitmapWidth)*i;
        		Enemy enemy1=new Enemy(enemyBitmapArray.get(1),enemyX1,-enemyMBitmapHeight,1);
        		Enemy enemy2=new Enemy(enemyBitmapArray.get(1),enemyX2,-enemyMBitmapHeight,1);
        		enemyArray.add(enemy1);
        		enemyArray.add(enemy2);
    		}
    		//����3�����͵л�
        	Enemy enemy1=new Enemy(enemyBitmapArray.get(2),10,-enemyMBitmapHeight-enemyLBitmapHeight,2);
        	Enemy enemy2=new Enemy(enemyBitmapArray.get(2),screenWidth-enemyLBitmapWidth-10,-enemyMBitmapHeight-enemyLBitmapHeight,2);
        	Enemy enemy3=new Enemy(enemyBitmapArray.get(2),screenWidth/2-enemyLBitmapWidth/2,-enemyMBitmapHeight-enemyLBitmapHeight,2);
        	enemyArray.add(enemy1);
        	enemyArray.add(enemy2);
        	enemyArray.add(enemy3);
    		isEnemiesComing=false;
    		//���µл�����ʱ��
    		createEnemyTime=createEnemyMTime=createEnemyLTime=gameTimeNow;
    	}
    	//�������飬���Ƶл�
    	for(int i=0;i<enemyArray.size();i++){
    		Enemy iEnemy=enemyArray.get(i);
    		iEnemy.drawEnemy(canvas);
    		//���µл�������
    		iEnemy.moveEnemy();
    		//�л��ɳ���Ļ��������������Ƴ�
    		if(iEnemy.getEnemyY()>=screenHeight)enemyArray.remove(i);
    	}
    }
 
    //������(����)
    private void drawCloud(Bitmap cloudBitmap,Canvas canvas){
    	
    	//������(����)������ʱ����
    	if(gameTimeNow-createCloudTime>=20000){
    		//������(����)
    		Random r=new Random();
    		int cloudX=r.nextInt(screenWidth-cloudBitmapWidth);
    		Cloud cloud=new Cloud(cloudBitmap,cloudX,0);
    		cloudArray.add(cloud);
    		//������(����)����ʱ��
    		createCloudTime=gameTimeNow;
    	}
    	//�������飬������(����)
    	for(int i=0;i<cloudArray.size();i++){
    		Cloud iCloud=cloudArray.get(i);
    		iCloud.drawCloud(canvas);
    		//������(����)������
    		iCloud.moveCloud();
    		//����(����)�ɳ���Ļ��������������Ƴ�
    		if(iCloud.getCloudY()>=screenHeight)cloudArray.remove(i);
    	}
    }
    
    //���Ʒ���
    private void drawScore(Canvas canvas){
    	
    	//���û�������
    	Paint paint=new Paint();
    	paint.setTextSize(30);
    	paint.setColor(Color.GRAY);
    	//���߼ӷ�ʱ��ʾ��ɫ
    	if(isScoreAdding)paint.setColor(Color.RED);
    	paint.setFakeBoldText(true);
    	canvas.drawText("score: "+score,5,30,paint);
    }
    
    //���Ƶл�Ⱥ����ʾ
    private void drawNotice(ArrayList<Bitmap> noticeBitmapArray,int[] noticeSequence,Canvas canvas){
    	if(noticeCount>0){
    		//���ݵл�Ⱥ����ʾͼ��ˢ�����У����λ�����Ӧ��ͼƬ
    		canvas.drawBitmap(noticeBitmapArray.get(noticeSequence[noticeSequence.length-noticeCount]), noticeX, noticeY, null);
    		System.out.println("noticeCount:"+noticeCount);
    		noticeCount--;
    	}
    	else{
    		//�л�Ⱥ����ʾչʾ��ϣ��ر���ʾ
    		isNoticeShowing=false;
    		noticeCount=noticeSequence.length;
    	}
    }
 
    //���Ƶ��߼ӷ�Ч��
    private void drawScoreAdding(Bitmap scoreAddingBitmap,Canvas canvas){
    	if(--scoreAddingCount>0){
    		canvas.drawBitmap(scoreAddingBitmap, scoreAddingX, scoreAddingY, null);
    		//���µ��߼ӷ�Ч��ͼ��������
    		scoreAddingY+=3;
    	}
    	else{
    		//���߼ӷ�Ч��չʾ��ϣ��ر�Ч��
    		isScoreAdding=false;
    	}
    }
    
    //��ҷɻ�����(����)����ײ���
    private void playerHitCloud(Bitmap cloudBombBitmap,Canvas canvas){
    	
    	//������(����)����
    	for(int i=0;i<cloudArray.size();i++){
    		Cloud iCloud=cloudArray.get(i);
    		int iCloudX=iCloud.getCloudX();
    		int iCloudY=iCloud.getCloudY();
    		int playerCenterX=playerX+playerBitmapWidth/2;
    		int playerCenterY=playerY+playerBitmapHeight/2;
    		//����ҷɻ����������������(����)ͼ�ķ�Χ����϶���ҷɻ���ô˵���
    		if(playerCenterX>=iCloudX&&playerCenterX<=iCloudX+cloudBitmapWidth&&playerCenterY>=iCloudY&&playerCenterY<=iCloudY+cloudBitmapHeight){
    			Random r=new Random();
    			switch(r.nextInt(3)){
    			case 0:
    				//���˫��ը��
    				isDoubleBullet=true;
    				doubleBulletCount=30;
    				break;
    			case 1:
    				//��ö���ӷ�
    				score+=3000;
    				isScoreAdding=true;
    				scoreAddingCount=10;
    				scoreAddingX=iCloudX;
    				//�����߼ӷ�Ч��ͼ���磬������������
                    if(scoreAddingX+scoreAddingBitmapWidth>screenWidth)scoreAddingX=screenWidth-scoreAddingBitmapWidth;
                    scoreAddingY=iCloudY-scoreAddingBitmapHeight;
                    if(scoreAddingY<0)scoreAddingY=iCloudY+cloudBitmapHeight;
    				break;
    			case 2:	
    				//�����л�Ⱥ����һ�󲨷ɻ����ڿ���
    				isEnemiesComing=true;
    				isNoticeShowing=true;
    				break;
    			}
    			//������(����)�ֽ�Ч��
    			canvas.drawBitmap(cloudBombBitmap, iCloudX, iCloudY, null);
    			//����(����)���������Ƴ�
    			cloudArray.remove(i);
    			i--;
    		}
    	}
    }
    
    //ը����л�����ײ���
    private void bulletHitEnemy(ArrayList<Bitmap> enemyBombBitmapArray,Canvas canvas){
    	
    	//����ը������
    	for(int i=0;i<bulletArray.size();i++){
    		Bullet iBullet=bulletArray.get(i);
    		int iBulletX=iBullet.getBulletX();
    		int iBulletY=iBullet.getBulletY();
    		//�����л�����
    		for(int j=0;j<enemyArray.size();j++){
    			Enemy iEnemy=enemyArray.get(j);
    			int iEnemyX=iEnemy.getEnemyX();
    			int iEnemyY=iEnemy.getEnemyY();
    			int enemyBitmapWidth=iEnemy.getEnemyBimmapWidth();
    			int enemyBitmapHeight=iEnemy.getEnemyBitmaoHeight();
    			//��ը�������Ͻǻ����Ͻǽ���л�ͼ�ķ�Χ�ڣ����϶�ը�����ел�
	    		if((iBulletX>=iEnemyX&&iBulletX<=iEnemyX+enemyBitmapWidth||
    				iBulletX+bulletBitmapWidth>=iEnemyX&&iBulletX+bulletBitmapWidth<=iEnemyX+enemyBitmapWidth)&&
    				iBulletY>=iEnemyY&&iBulletY<=iEnemyY+enemyBitmapHeight){
	    			//�жϵ�ǰ�л���Ѫ���Ƿ�Ϊ�㣬������Ƶл���ըЧ���������л����������Ƴ�
	    			if(iEnemy.hitEnemy()){
	    				canvas.drawBitmap(enemyBombBitmapArray.get(iEnemy.getType()), iEnemyX, iEnemyY, null);
	    				//�ӷ�
	    			    score+=iEnemy.getScore();
	    				enemyArray.remove(j);
	    			}
	    			//�����ел���ը�����������Ƴ�
	    			bulletArray.remove(i);
	    			/*ע��Arraylistɾ��Ԫ�غ�size�ı仯����ֹ�����������*/
	    			i--;
	    			j--;	    			
	    			break;
	    		}	    		
	    	}
    	}
    }
    
    //�л�����ҷɻ�����ײ���
    private boolean enemyHitPlayer(Bitmap playerBombBitmap,Canvas canvas){
    	
    	//�����л�����
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
    	    //����ҷɻ���л�ͼ������Բ��Ϊ��ײ���ķ�Χ��ͼƬ��߾����
    		double result=Math.sqrt((iEnemyCenterX-playerCenterX)*(iEnemyCenterX-playerCenterX)+(iEnemyCenterY-playerCenterY)*(iEnemyCenterY-playerCenterY));
    		if(result<playerBitmapWidth/2+enemyBitmapWidth/2){
    		/*//����ҷɻ���л�ͼ��Ϊ��ײ���ķ�Χ��ͼ���ĸ��ǵĿհ�����ײ���Ч��������
    		  if((iEnemyX>=playerX&&iEnemyX<=playerX+playerBitmapWidth||
    				iEnemyX+enemyBitmapWidth>=playerX&&iEnemyX+enemyBitmapWidth<=playerX+playerBitmapWidth)&&
    				(iEnemyY>=playerY&&iEnemyY<=playerY+playerBitmapHeight||
    				iEnemyY+enemyBitmapHeight>=playerY&&iEnemyY+enemyBitmapHeight<=playerY+playerBitmapHeight)
    				){*/
    			System.out.println("stop:"+result);
    			canvas.drawBitmap(playerBombBitmap, playerX, playerY, null);
    			//��ͣ��Ϸ������ֹͣ����
    			isPaused=true;
    			
    			handler.post(new Runnable(){

					@Override
					public void run() {
                        //�����Ի���
		    			new AlertDialog.Builder(PlaneBattleActivity.this).setTitle("��Ϸ����").setMessage("�Ƿ����¿�ʼ?")
		    			.setCancelable(false)
		    			.setPositiveButton("��", new OnClickListener(){
		
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//��ʼ����Ϸ����
								bulletArray.clear();
								enemyArray.clear();
								playerX=screenWidth/2-playerBitmapWidth/2;
								playerY=screenHeight-playerBitmapHeight;
								createBulletTime=createCloudTime=createEnemyLTime=createEnemyMTime=
									createEnemyTime=System.currentTimeMillis();
								score=0;
								//��Ϸ���¿�ʼ
								isPaused=false;
								
							}
		    				
		    			}).setNegativeButton("�˳�", new OnClickListener(){
		
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
									//����Ϸ�����Ի����°����ؼ�����Ϊѡ��"���¿�ʼ"��Ϸ
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
    
    
    //�����¼�
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			eventX=(int)event.getX();
			eventY=(int)event.getY();
			//�������Ӵ������������ҷɻ�ͼ�ķ�Χ�ڣ���������ҷɻ�
			/*if(eventX>=playerX&&eventX<=playerX+playerBitmapWidth&&
			eventY>=playerY&&eventY<=playerY+playerBitmapHeight){	*/		
				isPlayerLocked=true;
			//}
			break;
		case MotionEvent.ACTION_MOVE:
			//����ҷɻ�����������£���ʱ��¼�����Ӵ����λ�ƣ���ͬ������ҷɻ���λ��
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
			//������ҷɻ�
			isPlayerLocked=false;
			break;
		}	
		return super.onTouchEvent(event);
	}
	

	@Override
	protected void onPause() {
		//��Home���˳�ʱ���ã�������ͼ�߳�ѭ��
		System.out.println("onPause");
		flag=false;
		super.onPause();
	}

	
	@Override
	protected void onResume() {
		//��Home���ָ�����ʱ��������ͼ�߳�ѭ��
		System.out.println("onResume");
		flag=true;
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			//����Ϸ��սҳ�水���ؼ�����Ϸ��ͣ���������Ի���
			isPaused=true;
			handler.post(new Runnable(){

				@Override
				public void run() {
					new AlertDialog.Builder(PlaneBattleActivity.this).setTitle("��Ϸ��ͣ").setMessage("�Ƿ������Ϸ��")
					.setCancelable(false)
	    			.setPositiveButton("��", new OnClickListener(){
	
						@Override
						public void onClick(DialogInterface dialog, int which) {
							isPaused=false;	
						}
	    				
	    			}).setNegativeButton("�˳�", new OnClickListener(){
	
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PlaneBattleActivity.this.finish();
						}
	    				
	    			}).setOnKeyListener(new OnKeyListener(){

						@Override
						public boolean onKey(DialogInterface dialog,
								int keyCode, KeyEvent event) {
							//����Ϸ��ͣ�Ի����°����ؼ�����Ϊѡ��"����"��Ϸ
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