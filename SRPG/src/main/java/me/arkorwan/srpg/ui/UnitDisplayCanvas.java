package me.arkorwan.srpg.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.google.common.math.DoubleMath;

import me.arkorwan.srpg.models.Unit;
import me.arkorwan.utils.Common;

public class UnitDisplayCanvas extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int quota;
	List<Unit> units;
	boolean team1;
	boolean[] selected;
	int selections;

	Image characterTileset;
	boolean initialized = false;
	double squareSize;
	int columns;
	int currentUnitIndex = -1;

	UnitInformationPrinter unitPrinter;

	public UnitDisplayCanvas(UnitInformationPrinter unitPrinter) {

		characterTileset = Toolkit.getDefaultToolkit().getImage(
				this.getClass().getClassLoader().getResource("sprite.png"));

		setLayout(null);

		this.unitPrinter = unitPrinter;

		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {

				if (initialized) {
					int i = indexForPoint(e.getPoint());
					if (i != currentUnitIndex) {
						currentUnitIndex = i;
						if (unitPrinter != null) {
							if (i >= 0) {
								unitPrinter.displayUnit(units.get(i));
							} else {
								unitPrinter.clearDisplayedUnit();
							}
						}
						repaint();
					}

				}

			}

		});

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (quota > 0) {
					int i = currentUnitIndex;
					if (i >= 0) {
						selected[i] ^= true;
						selections += selected[i] ? 1 : -1;
						repaint();
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (unitPrinter != null) {
					unitPrinter.clearDisplayedUnit();
				}
				currentUnitIndex = -1;
				repaint();
			}

		});

	}

	public void setUnits(int quota, List<Unit> units, boolean team1) {
		this.units = units;
		this.team1 = team1;
		this.quota = quota;
		selected = new boolean[units.size()];
		selections = 0;
	}

	public List<Unit> getSelectedUnits() {
		List<Unit> team = new ArrayList<>();
		for (int i = 0; i < units.size(); i++) {
			if (selected[i]) {
				team.add(units.get(i));
			}
		}
		return team;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (units != null) {
			init();
			Graphics2D g2 = (Graphics2D) g;
			for (int i = 0; i < units.size(); i++) {
				Rectangle rect = gridForIndex(i, 0.9);

				if (selected[i]) {
					g2.setColor(new Color(0f, 0.8f, 0.2f, 0.7f));
					g2.fill(rect);
				}

				if (currentUnitIndex == i) {
					g2.setColor(Color.red);
				} else {
					g2.setColor(Color.black);
				}
				g2.draw(rect);

				drawTile(g2, FFTSpriteTile.tileFromUnit(units.get(i), team1),
						characterTileset, rect);
			}
		}
	}

	void init() {
		if (!initialized) {
			initialized = true;
			squareSize = Common.squareFittingSize(getWidth(), getHeight(),
					units.size());
			columns = DoubleMath.roundToInt(getWidth() / squareSize,
					RoundingMode.FLOOR);

		}

	}

	int indexForPoint(Point p) {
		int x = DoubleMath.roundToInt(p.x / squareSize, RoundingMode.FLOOR);
		int y = DoubleMath.roundToInt(p.y / squareSize, RoundingMode.FLOOR);

		int index = x + y * columns;
		if (x >= 0 && x < columns && y >= 0 && index < units.size()) {
			return index;
		} else {
			return -1;
		}
	}

	protected void drawTile(Graphics2D g2, Tile tile, Image tileImage,
			Rectangle rect) {
		g2.drawImage(tileImage, rect.x, rect.y, rect.x + rect.width,
				rect.y + rect.height, tile.getX() * tile.tileSize(),
				tile.getY() * tile.tileSize(),
				(tile.getX() + 1) * tile.tileSize(),
				(tile.getY() + 1) * tile.tileSize(), this);
	}

	protected Rectangle gridForIndex(int index, double relativeSize) {

		int x = index % columns;
		int y = index / columns;

		double localOffset = ((0.5 - relativeSize / 2) * squareSize);
		int destX1 = DoubleMath.roundToInt(x * squareSize + localOffset,
				RoundingMode.HALF_EVEN);
		int destY1 = DoubleMath.roundToInt(y * squareSize,
				RoundingMode.HALF_EVEN);
		int destX2 = DoubleMath.roundToInt((x + 1) * squareSize - localOffset,
				RoundingMode.HALF_EVEN);
		int destY2 = DoubleMath.roundToInt(
				(y + 1) * squareSize - (1.0 - relativeSize) * squareSize,
				RoundingMode.HALF_EVEN);
		return new Rectangle(destX1, destY1, destX2 - destX1, destY2 - destY1);
	}
}
