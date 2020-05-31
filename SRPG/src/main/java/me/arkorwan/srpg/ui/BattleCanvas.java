package me.arkorwan.srpg.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

import com.google.common.eventbus.Subscribe;
import com.google.common.math.DoubleMath;

import me.arkorwan.srpg.controller.BattleCommand;
import me.arkorwan.srpg.controller.BattleCommand.CommandType;
import me.arkorwan.srpg.controller.BattleController;
import me.arkorwan.srpg.controller.BattleEvents;
import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.BattleInfoReader;
import me.arkorwan.srpg.models.Coordinate;
import me.arkorwan.srpg.models.MagicSpell;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.utils.Common;

/**
 * 
 * @author arkorwan
 *
 */
public class BattleCanvas extends JPanel {

	private static enum State {
		Uninitialized, Normal, MoveSelection, AttackTargetSelection, MagicAttackTargetSelection
	}

	private static final long serialVersionUID = 9004182245355288881L;

	Image characterTileset;
	int scale = -1;
	int xOffset, yOffset;
	BattleInfoReader model;
	BattleController controller;
	BattleCommand currentCommand;
	Coordinate currentPointerCoordinate, lastClickedCoordinate;
	State state;
	MagicSpell currentSpell;
	Set<BattleCell> highlightedCells;

	static final int DAMAGE_STEPS = 12;
	int damageStep = 0;
	BattleEvents.DamageEvent currentDamageEvent;

	public BattleCanvas() {
		characterTileset = Toolkit.getDefaultToolkit().getImage(
				this.getClass().getClassLoader().getResource("sprite.png"));

		state = State.Uninitialized;

		setLayout(null);

		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (state != State.Uninitialized) {
					Coordinate c = coordinateForPoint(e.getPoint());
					if (currentPointerCoordinate != c) {
						currentPointerCoordinate = c;
						repaint();

						if (c != null) {
							BattleCell cell = model.getField()
									.cellForCoordinate(c);
							if (cell.getUnit().isPresent()) {
								BattleViewer.instance().unitPrinter
										.displayUnit(cell.getUnit().get());
							} else {
								BattleViewer.instance().unitPrinter
										.clearDisplayedUnit();
							}
						} else {
							BattleViewer.instance().unitPrinter
									.clearDisplayedUnit();
						}
					}
				}
			}

		});

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (controller.currentPlayer() instanceof ManualPlayer) {
					ManualPlayer player = (ManualPlayer) controller
							.currentPlayer();

					if (state == State.Normal) {
						lastClickedCoordinate = coordinateForPoint(
								e.getPoint());
						if (model.getUnitLocation(controller.getCurrentUnit())
								.getLocation().equals(lastClickedCoordinate)) {
							JPopupMenu popup = new JPopupMenu();
							for (CommandType cmd : player
									.getCurrentCommands()) {
								JMenuItem menuItem = new JMenuItem(
										cmd.getDisplayName());
								menuItem.addActionListener(
										(evt) -> handleCommand(cmd, e));
								popup.add(menuItem);
							}
							popup.show(BattleCanvas.this, e.getX(), e.getY());
						}
					} else {
						currentCommand.setTargetCoordinate(
								coordinateForPoint(e.getPoint()));
						currentCommand.setMagicSpell(currentSpell);
						currentSpell = null;
						player.setPlay(currentCommand);
						state = State.Normal;
						repaint();
					}
				}

			}

		});

	}

	private void initModel() {
		if (controller != null && state == State.Uninitialized) {
			model = controller.getBattle();
			controller.registerSubscriber(this);
			int scaleX = getWidth() / model.getField().getWidth();
			int scaleY = getHeight() / model.getField().getLength();
			scale = Math.min(scaleX, scaleY);
			xOffset = (getWidth() - (model.getField().getWidth() * scale)) / 2;
			yOffset = (getHeight() - (model.getField().getLength() * scale))
					/ 2;
			state = State.Normal;
			controller.start();
		}

	}

	public int getScale() {
		return scale;
	}

	/**
	 * Draw the scene in 3 layers: ground, object, effects
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		initModel();

		Graphics2D g2 = (Graphics2D) g;

		Color oldColor = g2.getColor();
		Stroke oldStroke = g2.getStroke();
		Font oldFont = g2.getFont();

		g2.setColor(Color.darkGray);
		g2.fillRect(0, 0, getWidth(), getHeight());

		if (model != null) {

			g2.setColor(Color.black);
			g2.fillRect(xOffset, yOffset, model.getField().getWidth() * scale,
					model.getField().getLength() * scale);

			drawGround(g2);
			drawHighlightEffects(g2);
			drawUnits(g2);
			drawDamage(g2);
			drawCurrentPointer(g2);

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.white);

		}

		g2.setStroke(oldStroke);
		g2.setColor(oldColor);
		g2.setFont(oldFont);

	}

	private void drawGround(Graphics2D g2) {
		// ground and void

		g2.setColor(Color.black);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16.0f));

		model.getField().forEachCell(c -> {

			Rectangle rect1 = gridForCoordinate(c.getLocation(), 0.9);
			if (c.isVoid()) {
				g2.fill(rect1);
			} else {
				// drawTile(g2, NonAgentTile.Empty, tileset, rect1);

				float v = 0.6f - c.getHeight() / 32f;
				if (v < 0f) {
					v = 0f;
				}
				g2.setColor(Color.getHSBColor(v, 0.5f, 0.5f));
				g2.fill(rect1);
				Rectangle rect2 = gridForCoordinate(c.getLocation(), 0.95);
				g2.setColor(Color.black);
				g2.drawString(String.valueOf(c.getHeight()), rect2.x,
						rect2.y + 15);
			}
		});
	}

	private void drawUnits(Graphics2D g2) {

		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 8.0f));
		int xOffset = 15;

		model.getField().forEachCell(c -> {
			if (c.getUnit().isPresent()) {

				Unit u = c.getUnit().get();

				Rectangle rect = gridForCoordinate(c.getLocation(), 0.85);
				drawTile(g2,
						FFTSpriteTile.tileFromUnit(u,
								model.getPartyForUnit(u) == model.getParty1()),
						characterTileset, rect);

				int tubeSize = rect.width - xOffset;

				double hpPercent = u.getCurrentHP() * 1.0
						/ u.getHP().getValue();
				int tubeFilled = DoubleMath.roundToInt(hpPercent * tubeSize,
						RoundingMode.UP);

				g2.setColor(Color.cyan);
				g2.drawString("HP", rect.x, rect.y + 4);
				g2.drawRect(rect.x + xOffset, rect.y, tubeSize, 3);
				g2.setColor(hpPercent > 0.2 ? Color.green : Color.red);
				g2.fillRect(rect.x + xOffset + 1, rect.y + 1, tubeFilled, 3);
			}
		});

	}

	private void drawHighlightEffects(Graphics2D g2) {

		Unit current = controller.getCurrentUnit();
		if (current != null) {

			if (state == State.MoveSelection) {
				g2.setColor(new Color(0f, 0.8f, 0.2f, 0.7f));
				highlightedCells.forEach(c -> {
					g2.fill(gridForCoordinate(c.getLocation(), 1.0));
				});
			} else if (state == State.AttackTargetSelection) {
				g2.setColor(new Color(0.8f, 0.2f, 0.2f, 0.7f));
				highlightedCells.forEach(c -> {
					g2.fill(gridForCoordinate(c.getLocation(), 1.0));
				});
			} else if (state == State.MagicAttackTargetSelection) {
				g2.setColor(new Color(0.8f, 0.2f, 0.2f, 0.7f));
				highlightedCells.forEach(c -> {
					g2.fill(gridForCoordinate(c.getLocation(), 1.0));
				});
			}

			BattleCell currentCell = model.getUnitLocation(current);
			g2.setColor(Color.yellow);
			Rectangle rect = gridForCoordinate(currentCell.getLocation(), 1.0);
			g2.fill(new Ellipse2D.Double(rect.x + scale * 0.1,
					rect.y + scale * 0.65, rect.width * 0.8,
					rect.height * 0.3));

		}

	}

	private void drawDamage(Graphics2D g2) {
		if (currentDamageEvent != null) {

			float alpha = damageStep * 1.0f / DAMAGE_STEPS;

			Rectangle rect = gridForCoordinate(currentDamageEvent.unitLocation,
					1.2);
			g2.setColor(new Color(0.8f, 0.8f, 0.8f, alpha * 0.5f + 0.5f));
			g2.fillOval(rect.x, rect.y, rect.width, rect.height);

			if (currentDamageEvent.isAttack) {
				g2.setColor(new Color(0.8f, 0.2f, 0.2f, alpha));
			} else {
				g2.setColor(new Color(0f, 0.8f, 0f, alpha));
			}
			g2.setFont(g2.getFont().deriveFont(20.0f));
			rect = gridForCoordinate(currentDamageEvent.unitLocation, 0.5);
			g2.drawString(currentDamageEvent.asString(), rect.x,
					rect.y + rect.height);

		}
	}

	private void drawCurrentPointer(Graphics2D g2) {

		if (currentPointerCoordinate != null) {
			g2.setStroke(new BasicStroke(5));
			g2.setColor(Color.red);
			g2.draw(gridForCoordinate(currentPointerCoordinate, 1.0));
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

	protected Rectangle gridForCoordinate(Coordinate coord,
			double relativeSize) {
		int localOffset = (int) ((0.5 - relativeSize / 2) * scale);
		int destX1 = xOffset + coord.x * scale + localOffset;
		int destY1 = yOffset + coord.y * scale;
		int destX2 = xOffset + (coord.x + 1) * scale - localOffset;
		int destY2 = yOffset + (coord.y + 1) * scale
				- (int) ((1.0 - relativeSize) * scale);
		return new Rectangle(destX1, destY1, destX2 - destX1, destY2 - destY1);
	}

	protected Coordinate coordinateForPoint(Point p) {
		int x = (p.x - xOffset) / scale;
		int y = (p.y - yOffset) / scale;
		// cannot compare x, y with 0 directly because of negative integer
		// division
		if (p.x >= xOffset && p.y >= yOffset && x < model.getField().getWidth()
				&& y < model.getField().getLength()) {
			return Coordinate.of(x, y);
		} else {
			return null;
		}
	}

	void handleCommand(CommandType cmd, MouseEvent e) {

		currentCommand = BattleCommand.ofType(cmd);
		switch (cmd) {
		case Move:
			state = State.MoveSelection;
			highlightedCells = model
					.getVisitableArea(controller.getCurrentUnit())
					.getCurrentArea();
			repaint();
			break;
		case EndTurn:
			((ManualPlayer) controller.currentPlayer()).setPlay(currentCommand);
			state = State.Normal;
			repaint();
			break;
		case Attack:
			state = State.AttackTargetSelection;
			highlightedCells = model
					.getPhysicallyAttackableArea(controller.getCurrentUnit())
					.getCurrentArea();
			repaint();
			break;
		case BlackMagic:
			handleMagicCommand(
					controller.getCurrentUnit().getCurrentBlackMagicSpells(),
					e);
			break;
		case WhiteMagic:
			handleMagicCommand(
					controller.getCurrentUnit().getCurrentWhiteMagicSpells(),
					e);
			break;
		default:
			break;
		}
	}

	void handleMagicCommand(List<MagicSpell> spells, MouseEvent e) {
		JPopupMenu popup = new JPopupMenu();
		for (MagicSpell spell : spells) {
			JMenuItem menuItem = new JMenuItem(
					String.format("%s [%d MP]", spell.getName(), spell.cost()));
			menuItem.addActionListener((evt) -> handleMagic(spell));
			popup.add(menuItem);
		}
		popup.show(BattleCanvas.this, e.getX() + 20, e.getY() + 20);
	}

	void handleMagic(MagicSpell spell) {
		currentSpell = spell;
		state = State.MagicAttackTargetSelection;
		highlightedCells = model
				.getMagicallyAttackableArea(controller.getCurrentUnit())
				.getCurrentArea();
		repaint();
	}

	@Subscribe
	public void handleBattleEvent(BattleEvents.DamageEvent e) {

		damageStep = DAMAGE_STEPS;
		currentDamageEvent = e;
		new AbstractAction() {
			private static final long serialVersionUID = 1L;
			Timer t = new Timer(delay / DAMAGE_STEPS, this);

			public void actionPerformed(ActionEvent e) {
				damageStep -= 1;
				repaint();
				if (damageStep <= 0) {
					t.stop();
					currentDamageEvent = null;
				}
			}

			void start() {
				t.start();
			}
		}.start();
	}

	static int delay = Common.getConfigWithDefault("visualizer.sleep_time_ms",
			400);

	@Subscribe
	public void handleActionTargetAreaEvent(
			BattleEvents.ActionTargetAreaEvent e) throws InterruptedException {

		if (controller.currentPlayer().shouldAnimate()) {
			State s = null;
			switch (e.commandType) {
			case Move:
				s = State.MoveSelection;
				break;
			case Attack:
				s = State.AttackTargetSelection;
				break;
			case BlackMagic:
			case WhiteMagic:
				s = State.MagicAttackTargetSelection;
				break;
			default:
			}

			if (s != null) {
				state = s;
				highlightedCells = e.area;
				repaint();
				Thread.sleep(delay);
			}
		}
	}

	@Subscribe
	public void handleUnitAction(BattleEvents.UnitActionEvent e)
			throws InterruptedException {
		if (e.player.shouldAnimate()) {
			state = State.Normal;
			repaint();
			Thread.sleep(delay);
		} else {
			repaint();
		}
	}

	@Subscribe
	public void handleBattleEnded(BattleEvents.BattleEndEvent e) {
		controller.unregisterSubscriber(this);
	}

}
