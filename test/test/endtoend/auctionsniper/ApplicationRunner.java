package test.endtoend.auctionsniper;


import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static test.endtoend.auctionsniper.FakeAuctionServer.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

public class ApplicationRunner {
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + XMPP_HOSTNAME + "/Auction";
	private AuctionSniperDriver driver;
	private AuctionLogDriver logDriver = new AuctionLogDriver();

	// trigger an event to drive the test
	public void startBiddingIn(final FakeAuctionServer... auctions) {
		startSniper();
		for (FakeAuctionServer auction : auctions) {
			openBiddingFor(auction, Integer.MAX_VALUE);
		}
	}

	public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
		startSniper();
		openBiddingFor(auction, stopPrice);
	}

	private void openBiddingFor(FakeAuctionServer auction, int stopPrice) {
		final String itemId = auction.getItemId();
		driver.startBiddingFor(itemId, stopPrice);
		driver.showsSniperStatus(auction.getItemId(), 0, 0, SnipersTableModel.textFor(SniperState.JOINING));
	}

	private void startSniper() {
		logDriver.clearLog();
		Thread thread = new Thread("Test Application") {
			@Override public void run() {
				try {
					Main.main(arguments());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();

		driver = new AuctionSniperDriver(1000);
		driver.hasTitle(MainWindow.APPLICATION_TITLE);
		driver.hasColumnTitles();
	}

	protected static String[] arguments(FakeAuctionServer... auctions) {
		String[] arguments = new String[auctions.length + 3];
		arguments[0] = XMPP_HOSTNAME;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPER_PASSWORD;
		for (int i = 0; i < auctions.length; i++) {
			arguments[i+3] = auctions[i].getItemId();
		}
		return arguments;
	}

	public void showsSniperHasLostAcution(int lastPrice, int lastBid) {
		driver.showsSniperStatus(SnipersTableModel.textFor(SniperState.LOST)); // test case
	}

	public void showsSniperHasWonAcution(FakeAuctionServer auction, int lastPrice) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice,
				SnipersTableModel.textFor(SniperState.WON)); // test case
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}

	public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid,
				SnipersTableModel.textFor(SniperState.BIDDING)); // test case
	}

	public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
		driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid,
				SnipersTableModel.textFor(SniperState.WINNING)); // test case
	}

	public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid,
				SnipersTableModel.textFor(SniperState.LOSING)); // test case
	}

	public void showsSniperHasFailed(FakeAuctionServer auction) {
		driver.showsSniperStatus(auction.getItemId(), 0, 0,
				SnipersTableModel.textFor(SniperState.FAILED)); // test case
	}

	public void reportsInvalidMessage(FakeAuctionServer auction,
			String message) throws IOException {
		logDriver.hasEntry(containsString(message));
	}

	public class AuctionLogDriver {
		public static final String LOG_FILE_NAME ="auction-sniper.log";
		private final File logFile = new File(LOG_FILE_NAME);

		public void hasEntry(Matcher<String> matcher) throws IOException {
			assertThat(FileUtils.readFileToString(logFile), matcher);
		}

		public void clearLog() {
			logFile.delete();
			LogManager.getLogManager().reset();
		}
	}
}
