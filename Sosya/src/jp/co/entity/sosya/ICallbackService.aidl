package jp.co.entity.sosya;

import jp.co.entity.sosya.ICallbackListener;

interface ICallbackService {
	void addListener(ICallbackListener listener);
	void removeListener(ICallbackListener listener);
}