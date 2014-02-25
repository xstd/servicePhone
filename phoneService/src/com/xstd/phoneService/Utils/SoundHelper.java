package com.xstd.phoneService.Utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import com.xstd.phoneService.R;

/**
 * Created by zhangdi on 14-1-6.
 */
public class SoundHelper {

    private static SoundHelper gSoundHelper = new SoundHelper();

    private SoundPool mBeepSoundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);

    private int mScanCompletedId;

    private int mPushSoundId;

    private SoundHelper() {

    }

    public static SoundHelper getInstance() {
        return gSoundHelper;
    }

    public void init(Context context) {
        mScanCompletedId = mBeepSoundPool.load(context, R.raw.like, 0);
        mPushSoundId = mBeepSoundPool.load(context, R.raw.push, 0);
    }

    public void playSMSReceiveSound() {
        mBeepSoundPool.play(mScanCompletedId, 1, 1, 0, 0, 1);
    }

    public void playPushSound() {
        mBeepSoundPool.play(mPushSoundId, 1, 1, 0, 0, 1);
    }
}
