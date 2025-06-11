package gamelogic.level;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import gameengine.*;
import gameengine.graphics.Camera;
import gameengine.loaders.Mapdata;
import gameengine.loaders.Tileset;
import gameengine.powerups.Wings;
import gamelogic.GameResources;
import gamelogic.Main;
import gamelogic.enemies.Enemy;
import gamelogic.player.Player;
import gamelogic.tiledMap.Map;
import gamelogic.tiles.Flag;
import gamelogic.tiles.Flower;
import gamelogic.tiles.Gas;
import gamelogic.tiles.SolidTile;
import gamelogic.tiles.Spikes;
import gamelogic.tiles.Tile;
import gamelogic.tiles.Water;
import gameengine.powerups.Wings;
public class Level {

	private LevelData leveldata;
	private Map map;
	private Enemy[] enemies;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Wings> wingsList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();
	private ArrayList<Water> waters = new ArrayList<>();
	private ArrayList<Gas> gases = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
	}

	public LevelData getLevelData() {
		return leveldata;
	}

	public void restartLevel() {
		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];
		waters = new ArrayList();
		gases = new ArrayList();
		wingsList = new ArrayList<>();

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition * tileSize, yPosition * tileSize, this)); // TODO: objects vs
																									// tiles
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				else if (values[x][y] == 16)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
				else if (values[x][y] == 17)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
				else if (values[x][y] == 18){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
					waters.add((Water)tiles[x][y]);}
				else if (values[x][y] == 19){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
					waters.add((Water)tiles[x][y]);}
				else if (values[x][y] == 20){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
					waters.add((Water)tiles[x][y]);}
				else if (values[x][y] == 21){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
					waters.add((Water)tiles[x][y]);}
				else if (values[x][y] == 22)
					wingsList.add(new Wings(xPosition * tileSize, yPosition * tileSize, tileSize, tileSize, this));


			}

		}
		enemies = new Enemy[enemiesList.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		for (int i = 0; i < enemiesList.size(); i++) {
			enemies[i] = new Enemy(enemiesList.get(i).getX(), enemiesList.get(i).getY(), this);
		}


		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if (flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
					else
						addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new ArrayList<Gas>()); // 20
					flowers.remove(i);
					i--;
				}
			}
			
			boolean didITouchWater = false;
			for(Water w: waters){
				if (w.getHitbox().isIntersecting(player.getHitbox())){
					didITouchWater = true;
					player.walkSpeed = 100;
				}
			}
			if(!didITouchWater){
					player.walkSpeed = 500;
			}

			boolean didITouchGas = false;
			for(Gas g: gases){
				if (g.getHitbox().isIntersecting(player.getHitbox())){
					didITouchGas = true;
				}
			}
			camera.didITouchGas = didITouchGas;

			boolean didITouchWing = false;
			for(Wings f: wingsList){
				if (f.getHitbox().isIntersecting(player.getHitbox())){
					System.out.println("touching wing");
					didITouchWing = true;
					player.flying = true;
					player.time = System.currentTimeMillis();
				}
			}


			// Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					onPlayerDeath();
				}
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}



	// Precondition: map and placedThisRound is not null and you cant impunt a number grater than what is possible.
	// Postcondition: Creates 20 gas blocks in a certain pattern that alows it to fit into the space provided.
	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
		Gas g = new Gas(col, row, tileSize, tileset.getImage("GasOne"), this, 0);
		map.addTile(col, row, g);
		numSquaresToFill--;
		placedThisRound.add(g);
		gases.add(g); 

		while ((placedThisRound.size() > 0) && (numSquaresToFill > 0)) {
			row = placedThisRound.get(0).getRow();
			col = placedThisRound.get(0).getCol();
			placedThisRound.remove(0);
			// up
			if (row - 1  >= 0) {

				if (!(map.getTiles()[col][row - 1] instanceof Gas) && !map.getTiles()[col][row - 1].isSolid() && (numSquaresToFill > 0)) {
					g = new Gas(col, row - 1, tileSize, tileset.getImage("GasOne"), this, 0);
					map.addTile(col, row - 1, g);
					numSquaresToFill--;
					placedThisRound.add(g);
					gases.add(g);
				}
				if (col + 1 < map.getTiles().length && !(map.getTiles()[col + 1][row - 1] instanceof Gas) && !map.getTiles()[col + 1][row - 1].isSolid() && (numSquaresToFill > 0)) {
					g = new Gas(col+1, row-1, tileSize, tileset.getImage("GasOne"), this, 0);
					map.addTile(col + 1, row - 1, g);
					numSquaresToFill--;
					placedThisRound.add(g);
					gases.add(g);
				}
				if (col - 1 >= 0 && !(map.getTiles()[col - 1][row - 1] instanceof Gas) && !map.getTiles()[col - 1][row - 1].isSolid() && (numSquaresToFill > 0)) {
					g = new Gas(col-1, row-1, tileSize, tileset.getImage("GasOne"), this, 0);
					map.addTile(col - 1, row - 1, g);
					numSquaresToFill--;
					placedThisRound.add(g);
					gases.add(g);
				}
			}

			// center
			if (col + 1 < map.getTiles().length && !(map.getTiles()[col + 1][row] instanceof Gas) && !map.getTiles()[col + 1][row].isSolid() && (numSquaresToFill > 0)) {
				g = new Gas(col+1, row, tileSize, tileset.getImage("GasOne"), this, 0);
				map.addTile(col + 1, row, g);
				numSquaresToFill--;
				placedThisRound.add(g);
				gases.add(g);
			}
			if (col - 1 >= 0 && !(map.getTiles()[col - 1][row] instanceof Gas) && !map.getTiles()[col - 1][row].isSolid() && (numSquaresToFill > 0)) {
				g = new Gas(col-1, row, tileSize, tileset.getImage("GasOne"), this, 0);
				map.addTile(col - 1, row, g);
				numSquaresToFill--;
				placedThisRound.add(g);
				gases.add(g);
			}

			// down
			if (row + 1 < map.getTiles()[0].length) {
				if (!(map.getTiles()[col][row + 1] instanceof Gas) && !map.getTiles()[col][row + 1].isSolid() && (numSquaresToFill > 0)) {
					g = new Gas(col, row+1, tileSize, tileset.getImage("GasOne"), this, 0);
					map.addTile(col, row + 1, g);
					numSquaresToFill--;
					placedThisRound.add(g);
					gases.add(g);
				}
				if (col + 1 < map.getTiles().length && !(map.getTiles()[col + 1][row + 1] instanceof Gas) && !map.getTiles()[col + 1][row + 1].isSolid() && (numSquaresToFill > 0)) {
					g = new Gas(col+1, row+1, tileSize, tileset.getImage("GasOne"), this, 0);
					map.addTile(col + 1, row + 1, g);
					numSquaresToFill--;
					placedThisRound.add(g);
					gases.add(g);
				}
				if (col - 1 >= 0 && !(map.getTiles()[col - 1][row + 1] instanceof Gas) && !map.getTiles()[col - 1][row + 1].isSolid() && (numSquaresToFill > 0)) {
					g = new Gas(col-1, row+1, tileSize, tileset.getImage("GasOne"), this, 0);
					map.addTile(col - 1, row + 1, g);
					numSquaresToFill--;
					placedThisRound.add(g);
					gases.add(g);
				}
			}
		}
	}

	// Precondition: the map isn't a null value and fullness is between 0 and 3.
	// Postcondition: will make the water when hiting the flower.
	private void water(int col, int row, Map map, int fullness) {

		Water w = new Water(col, row, tileSize, tileset.getImage("Full_water"), this, fullness);
		if (fullness == 0)
			w = new Water(col, row, tileSize, tileset.getImage("Falling_water"), this, fullness);
		else if (fullness == 2)
			w = new Water(col, row, tileSize, tileset.getImage("Half_water"), this, fullness);
		else if (fullness == 1)
			w = new Water(col, row, tileSize, tileset.getImage("Quarter_water"), this, fullness);

		map.addTile(col, row, w);
		waters.add(w);

		// down
		if (row + 1 < map.getTiles()[0].length && !(map.getTiles()[col][row + 1] instanceof Water)
				&& !map.getTiles()[col][row + 1].isSolid()) {
			// water(col, row, map, 3);
			if (row + 2 < map.getTiles()[0].length && map.getTiles()[col][row + 2].isSolid()) {
				water(col, row + 1, map, 3);
			} else {
				water(col, row + 1, map, 0);
			}
		}
		// if we can't go down go left and right.
		// right
		else if ((row + 1 < map.getTiles()[0].length) && !(map.getTiles()[col][row + 1] instanceof Water)) {
			if (col + 1 < map.getTiles().length && !(map.getTiles()[col + 1][row] instanceof Water)
					&& !map.getTiles()[col + 1][row].isSolid()) {
				// water(col+1, row, map, 3);
				if (fullness > 1) {
					water(col + 1, row, map, fullness - 1);
				} else {
					water(col + 1, row, map, 1);
				}

			}
			// left
			if (col - 1 >= 0 && !(map.getTiles()[col - 1][row] instanceof Water)
					&& !map.getTiles()[col - 1][row].isSolid()) {

				if (fullness > 1) {
					water(col - 1, row, map, fullness - 1);
				} else {
					water(col - 1, row, map, 1);
				}

			}
		}
	}

	public void draw(Graphics g) {
		g.translate((int) -camera.getX(), (int) -camera.getY());
		// Draw the map
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				Tile tile = map.getTiles()[x][y];
				if (tile == null)
					continue;
				if (tile instanceof Gas) {

					int adjacencyCount = 0;
					for (int i = -1; i < 2; i++) {
						for (int j = -1; j < 2; j++) {
							if (j != 0 || i != 0) {
								if ((x + i) >= 0 && (x + i) < map.getTiles().length && (y + j) >= 0
										&& (y + j) < map.getTiles()[x].length) {
									if (map.getTiles()[x + i][y + j] instanceof Gas) {
										adjacencyCount++;
									}
								}
							}
						}
					}
					if (adjacencyCount == 8) {
						((Gas) (tile)).setIntensity(2);
						tile.setImage(tileset.getImage("GasThree"));
					} else if (adjacencyCount > 5) {
						((Gas) (tile)).setIntensity(1);
						tile.setImage(tileset.getImage("GasTwo"));
					} else {
						((Gas) (tile)).setIntensity(0);
						tile.setImage(tileset.getImage("GasOne"));
					}
				}
				if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
					tile.draw(g);
			}
		}

		// Draw the enemies
		for (int i = 0; i < enemies.length; i++) {
			enemies[i].draw(g);
		}

		// Draw the wings
		for (int i = 0; i < wingsList.size(); i++) {
			wingsList.get(i).draw(g);
		}


		// Draw the player
		player.draw(g);

		// used for debugging
		if (Camera.SHOW_CAMERA)
			camera.draw(g);
		g.translate((int) +camera.getX(), (int) +camera.getY());
	}

	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}
}