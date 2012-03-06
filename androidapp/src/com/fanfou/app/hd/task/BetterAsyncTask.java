package com.fanfou.app.hd.task;

import com.fanfou.app.hd.App;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author mcxiaoke
 * @version 1.0 2012.03.05
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class BetterAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	private final String callerId;
	private Exception error;

	public BetterAsyncTask(Context context) {
		super();
		this.callerId = context.getClass().getCanonicalName();
		App.setActiveContext(callerId, context);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Context context = getCallingCotext();
		if (context == null) {
			cancel(true);
			return;
		}
		before(context);
	}

	@Override
	protected final void onPostExecute(Result result) {
		super.onPostExecute(result);
		Context context = getCallingCotext();
		if (context == null) {
			return;
		}

		if (error == null) {
			after(context, result);
		} else {
			error(context, error);
		}
	}

	protected abstract void before(Context context);

	protected abstract void after(Context context, Result result);

	protected abstract void error(Context context, Exception exception);
	
	protected abstract Result run(Params... params);

	@Override
	protected final Result doInBackground(Params... params) {
		try {
			return run(params);
		} catch (Exception e) {
			this.error=e;
			return null;
		}
	}

	protected void setFailed(Exception e) {
		this.error = e;
	}

	protected Context getCallingCotext() {
		Context caller = App.getActiveContext(callerId);
		if (caller == null
				|| !this.callerId.equals(caller.getClass().getCanonicalName())) {
			return null;
		}
		return caller;
	}

}
