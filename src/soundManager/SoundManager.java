package soundManager;

import java.io.IOException;
import java.util.HashMap;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;


public class SoundManager {

	private static SoundManager s_instance; //�̱��� ��ü
	private SoundPool b_soundpool; //���� Ǯ
	private MediaPlayer b_media; //�̵��
	private HashMap	b_soundPoolMap; //����Ǯ �ؽ���
	private AudioManager b_audioManager;
	private Context b_Activity;

	
	public void Init(Context _context)
	{
		this.b_soundpool = new SoundPool(3,AudioManager.STREAM_MUSIC,0);
		this.b_soundPoolMap  = new HashMap();
		this.b_audioManager = (AudioManager)_context.getSystemService(Context.AUDIO_SERVICE);
		this.b_Activity = _context;
		this.b_media = new MediaPlayer();
	}
	public static SoundManager getInstance(){//�̱���
		if(s_instance == null){
			s_instance = new SoundManager();
		}
		return s_instance;
	}
	public void mPrepare()
	{
		try {
			b_media.prepare();
		} catch (Exception e) {
		}
	}
	public void mReset() //�̵�� �����
	{
		b_media.reset();
	}
	public void playmedia() //�̵�� ���
	{
		b_media.start();
	}
	public void addmedia(int soundID)//�̵�� �߰��ϱ�
	{
		b_media = MediaPlayer.create(b_Activity, soundID);
	}
	public void stopmedia()
	{
		b_media.stop();
	}
	public void pauseM()//�̵�� �Ͻ�����
	{
		b_media.pause();
	}
	
	public void addSound(int index,int soundID){
		int id = b_soundpool.load(b_Activity, soundID, 1);
		b_soundPoolMap.put(index, id);
	}
	
	public SoundPool getSoundPool() //����Ǯ �߰�
	{
		return this.b_soundpool;
	}
	public void stop(int index)//����Ǯ ����
	{
		b_soundpool.stop((Integer)b_soundPoolMap.get(index));
	}
	public void strstop(int index)
	{
		b_soundpool.stop(index);
	}
	public void play(int index) //���� Ǯ ���
	{
		float streamVolume = b_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume/b_audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		b_soundpool.play((Integer)b_soundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
	}
	public int playLoop(int index)//���� Ǯ ���� ���
	{
		float streamVolume = b_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume/b_audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int temp = b_soundpool.play((Integer)b_soundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f);
		return temp;
	}
	public void poolPause()//����Ǯ �Ͻ�����
	{
		b_soundpool.autoPause();
	}
	public void poolResume()//����Ǯ �ٽ� ���
	{
		b_soundpool.autoResume();
	}
	public void releaseMusic()
	{
		b_media.release();
		b_soundpool.release();
	}
}
