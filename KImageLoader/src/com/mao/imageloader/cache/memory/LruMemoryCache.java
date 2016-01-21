package com.mao.imageloader.cache.memory;

import com.mao.imageloader.utils.BitmapUtils;

import android.graphics.Bitmap;
import android.text.TextUtils;

/**
 * 基于LruCache的Bitmap内存缓存管理器
 * 
 * @author mao
 *
 */
public class LruMemoryCache extends BaseMemoryCache<String, Bitmap> {
	
	/** 缓存条件内存因子 */
	private final static float LIMIT_FACTOR = 15.0f;
	
	/** 最大缓存大小，单位：字节，默认为堆最大大小的1/6*/
	private final static int MAX_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 6);
	
	private static android.support.v4.util.LruCache<String, Bitmap> sBitmapCache = new android.support.v4.util.LruCache<String, Bitmap>(MAX_CACHE_SIZE){
		
		@Override
		protected int sizeOf(String key, Bitmap value) {
			return BitmapUtils.sizeOfBitmap(value);
		}
	};
	
	private static LruMemoryCache sIntsance = new LruMemoryCache();
	
	private LruMemoryCache() {}
	
	public static LruMemoryCache getInstance() {
		return sIntsance;
	}
	
	@Override
	public boolean put(String key, Bitmap value) {
		if(TextUtils.isEmpty(key) || value == null) {
			return false;
		} else {
			//判断是否有足够的内存
			if(checkRemainderMemory(value)) {
				sBitmapCache.put(key, value);
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	public Bitmap get(String key) {
		if(TextUtils.isEmpty(key)) {
			return null;
		} else {
			return sBitmapCache.get(key);
		}
	}
	
	private boolean checkRemainderMemory(Bitmap bm) {
		long size = BitmapUtils.sizeOfBitmap(bm);
		Runtime runtime = Runtime.getRuntime();
		long total = runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
		if(size * LIMIT_FACTOR < total) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean clear() {
		if(sBitmapCache != null) {
			sBitmapCache.evictAll();
			return true;
		}
		return false;
	}
}
