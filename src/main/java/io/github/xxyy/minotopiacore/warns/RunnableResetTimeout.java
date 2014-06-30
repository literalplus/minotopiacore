package io.github.xxyy.minotopiacore.warns;


public final class RunnableResetTimeout implements Runnable {
	public String plrName = null;
	public RunnableResetTimeout(String plrName){ this.plrName = plrName; }
	@Override
	public void run() {
		if(plrName == null) {
            return;
        }
		WarnHelper.playerTimeouts.remove(plrName);
	}

}
