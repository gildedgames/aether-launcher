package net.aetherteam.aether.launcher.download;

public abstract interface DownloadListener {

	public abstract void onDownloadJobFinished(DownloadJob paramDownloadJob);

	public abstract void onDownloadJobProgressChanged(DownloadJob paramDownloadJob);
}
