package wiiudev.gecko.client.gui.tabs.memory_search;

import wiiudev.gecko.client.gui.JGeckoUGUI;

import javax.swing.*;

public class ProgressVisualization
{
	public static void updateProgress(String labelName, long finishedCount, long totalCount)
	{
		updateProgressBar(finishedCount, totalCount);
		updateLabel(labelName, finishedCount, totalCount);
	}

	public static void deleteUpdateLabel()
	{
		JLabel addressProgressLabel = JGeckoUGUI.getInstance().getAddressProgressLabel();
		addressProgressLabel.setText("");
		setProgressBarCompleted();
	}

	private static void setProgressBarCompleted()
	{
		updateProgressBar(1, 1);
	}

	private static void updateProgressBar(long finishedCount, long totalCount)
	{
		JProgressBar progressBar = JGeckoUGUI.getInstance().getSearchProgressBar();
		int progress = (int) (finishedCount * 100 / totalCount);
		progressBar.setValue(progress);
	}

	private static void updateLabel(String labelName, long finishedCount, long totalCount)
	{
		JLabel addressProgressLabel = JGeckoUGUI.getInstance().getAddressProgressLabel();

		if (finishedCount == totalCount)
		{
			addressProgressLabel.setText("");
		}
		else
		{
			addressProgressLabel.setText(labelName + ": " + finishedCount + "/" + totalCount);
		}
	}
}
