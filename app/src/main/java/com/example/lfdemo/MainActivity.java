package com.example.lfdemo;

import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import android.R.integer;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends SerialPortActivity {

	Button btnOpen,btnClose;
	Button btPowerOn,btPowerOff;
	EditText mReception;
	private int soundid;		
	private SoundPool soundpool = null;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mReception = (EditText) findViewById(R.id.EditTextReception);
		btnOpen = (Button)this.findViewById(R.id.btnOpen);
		btnClose = (Button)this.findViewById(R.id.btnClose);
		btPowerOn = (Button)this.findViewById(R.id.btnPowerOn);
		btPowerOff = (Button)this.findViewById(R.id.btnPowerOff);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100);
        soundid = soundpool.load(this, R.raw.rfid_beep, 1);
	}
	
	public void btnOpen_Click(View btn)
	{
		if(this.open())
		{
			this.btnOpen.setEnabled(false);
			this.btnClose.setEnabled(true);
		}
	}
	
	public void btnClose_Click(View btn)
	{
		this.close();
		this.btnOpen.setEnabled(true);
		this.btnClose.setEnabled(false);
	}

	
public void btnPower_On(View btn) {
	
	try {
	FileWriter localFileWriterOn = new FileWriter(new File("/proc/gpiocontrol/set_id"));
    localFileWriterOn.write("1");
    localFileWriterOn.close();
	} catch (Exception e) { 
		e.printStackTrace(); 
	}
	
	this.btPowerOn.setEnabled(false);
	this.btPowerOff.setEnabled(true);
}



public void btnPower_Off(View btn) {
	
	 try {
		FileWriter localFileWriterOff = new FileWriter(new File("/proc/gpiocontrol/set_id"));
	    localFileWriterOff.write("0");
	    localFileWriterOff.close();
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		this.btPowerOn.setEnabled(true);
		this.btPowerOff.setEnabled(false);

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDataReceived(final byte[] buffer,final int size) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			public void run() {
				if (mReception != null) {
					if(size > 0){
						if(size >= 5){
							
							byte[] tempBuf = new byte[size-4];
							System.arraycopy(buffer, 4, tempBuf, 0, size-4);//��ȥǰ4λ
							String temStr= new String(tempBuf);
							int temInt = Integer.parseInt(temStr,16);//16����ת����10���ơ�
							soundpool.play(soundid, 1, 1, 0, 0, 1);
							mReception.append(Integer.toString(temInt));//�ַ�����ʾ ��
							
						}else{
							
							mReception.append(new String(buffer, 0, size));
						}
						
					}
				}
			}
			
		});
	}
	
	public static String binary(byte[] bytes, int radix){  
        return new BigInteger(1,bytes).toString(radix);
    }
}
