//file main.java
package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.Label;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


enum states{
	IDLE, ATTACK, WALK, DIEING
}

public class Main extends Application {

    // ---------- Game constants ----------
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 750;
    private static final int ROWS = 4;
    private static final int COLS = 9;
    private static final int CELL_WIDTH = 141;
    private static final int CELL_HEIGHT = 100;
    private static final int GRID_START_X = 20;
    private static final int GRID_START_Y = 225;

    // ---------- Game state ----------
    private Defender selectedDefense = null;
    private double dragX, dragY;
    private Defender[][] grid = new Defender[ROWS][COLS];
    private int gold = 1000;
    private int baseHealth = 100; 
    private boolean gameOver = false;
    Enemy[] enemy_types = {new SkeletonSpearman(), new SkeletonWarrior(), new Boss()};
    Random rand = new Random();
    
    private long lastNano = 0;
    private double delta = 0.0;

    
    private static final int WAVES = 3;

    private int currentWave = 0;           
    private int enemiesToSpawnThisWave = 0;
    private int enemiesSpawnedThisWave = 0;
    private boolean waveActive = false;    
    private boolean allWavesComplete = false;
    private long nextWaveStartTime = 0;   
    private static final long WAVE_BREAK_NANOS = 5_000_000_000L; 
    
    @SuppressWarnings("unchecked")
    ArrayList<Enemy>[] enemies = (ArrayList<Enemy>[]) new ArrayList[ROWS];
    
    private static final List<Projectile> projectiles = new ArrayList<>();
    
    int maxNummberEnemies = 2;
    long spawnCooldown = 2000000000;
    long lastSpawn = 0;
    // ---------- JavaFX nodes ----------
    private Canvas canvas;
    private GraphicsContext gc;
    private DefenseMenu menu;
    private Image background = new Image("file:sprites/tower.png");
    private Image goldImg = new Image("file:sprites/gold.png");
    @Override
    public void start(Stage stage) {
    	canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        menu = new DefenseMenu();

        Pane root = new Pane();
        root.getChildren().addAll(canvas, menu);

        menu.setLayoutX(0);
        menu.setLayoutY(HEIGHT - 70);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Knights vs skeletons");
        stage.show();

        setupDragAndDrop();
        
        for (int i = 0; i < enemies.length; i++) {
        	enemies[i] = new ArrayList<>();
        }

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            	
            	if (lastNano == 0) {
                    lastNano = now;
                    return;
                }
                delta = (now - lastNano) / 1_000_000_000.0;
                if (delta > 0.033) delta = 0.033;
                lastNano = now;
            	
            	update(now, delta);
                render();
            }
        };
        timer.start();
        startWave(1);
    }
     private int totalEnemiesOnField() {
        int count = 0;
        for (ArrayList<Enemy> row : enemies) {
            count += row.size();
        }
        return count;
    }
    private void startWave(int wave) {
        currentWave = wave;
        waveActive = true;
        enemiesSpawnedThisWave = 0;

        switch (wave) {
            case 1:
            	enemiesToSpawnThisWave = 20;   
            case 2:
                enemiesToSpawnThisWave = 40;
            case 3:
            	enemiesToSpawnThisWave = 60; 
        }
    }
    private void WaveProgress(long now) {
        if (!waveActive) return;
        
        if (enemiesSpawnedThisWave == enemiesToSpawnThisWave && totalEnemiesOnField() == 0) {
            waveActive = false;

            if (currentWave >= WAVES) {
                allWavesComplete = true;  
                return;
            }

            nextWaveStartTime = now + WAVE_BREAK_NANOS;
        }
    }

    private void checkWaves(long now) {
        if (waveActive || allWavesComplete || gameOver) return;

        if (currentWave == 0 || now >= nextWaveStartTime) {
            startWave(currentWave + 1);
        }
    }


    private void setupDragAndDrop() {
        canvas.setOnMouseMoved(this::handleCanvasMoved);
        canvas.setOnMousePressed(this::handleCanvasPressed);
    }


    public void startDrag(Defender type, double startX, double startY) {
        if(gameOver) return;
    	if(allWavesComplete) return;
        selectedDefense = type;
        dragX = startX;
        dragY = startY;
    }

    private void handleCanvasMoved(MouseEvent e) {
        if (gameOver) return;
    	if (allWavesComplete) return;
        if (selectedDefense != null) {
            dragX = e.getX();
            dragY = e.getY();
        }
    }

    private void handleCanvasPressed(MouseEvent e) {
        if (gameOver) return;
    	if (allWavesComplete) return;
        int[] cell = getGridCell(e.getX(), e.getY());
        if (selectedDefense != null && cell != null && canPlaceDefender(cell[0], cell[1], selectedDefense)) {
            placeDefender(selectedDefense, cell[0], cell[1]);
            gold -= selectedDefense.cost;
        }

        selectedDefense = null;
    }


    private int[] getGridCell(double screenX, double screenY) {
        int col = (int) ((screenX - GRID_START_X) / CELL_WIDTH);
        int row = (int) ((screenY - GRID_START_Y) / CELL_HEIGHT);

        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            return new int[]{row, col};
        }
        return null;
    }

    private boolean canPlaceDefender(int row, int col, Defender type) {
        return grid[row][col] == null && gold >= type.cost;
    }

    private void placeDefender(Defender type, int row, int col) {
        Defender d = type.copy(row, col);
        grid[row][col] = d;
    }



 
    private void update(long now, double delta) {
        if(gameOver) return;
    	checkWaves(now);
    	if (allWavesComplete) return;  
        // Spawn enemies
    	if (waveActive && enemiesSpawnedThisWave < enemiesToSpawnThisWave && now - lastSpawn > spawnCooldown) {
        int randomRow = rand.nextInt(ROWS);
        if (enemies[randomRow].size() < maxNummberEnemies) {
            Enemy enemy = enemy_types[rand.nextInt(enemy_types.length)];
            enemies[randomRow].add(enemy.copy(randomRow));
            enemiesSpawnedThisWave++;
            lastSpawn = now;
        	}
        }
    	
    	//Enemies update position and attacking
        for (int r = 0; r < ROWS; r++) {
            ArrayList<Enemy> rowEnemies = enemies[r];
            Iterator<Enemy> it = rowEnemies.iterator();
            while (it.hasNext()) {
                Enemy enemy = it.next();
                if (enemy.health <= 0) {
                	enemy.state = states.DIEING;
                	gold += enemy.reward;
                }
                if (enemy.toRemove || enemy.drawX < -10) {rowEnemies.remove(enemy);}
                if (enemy.drawX < -10) {baseHealth -=10;}
                if(baseHealth <=0) {
                	gameOver = true;
                	selectedDefense = null;
                	}

                //get closer defender
                Defender closest = null;
                double closestDistance = 1300;
                for (int col = 0; col < COLS; col++) {
                    Defender d = grid[r][col];
                    if (d == null || d.state == states.DIEING) continue;
      
                    if ((d.drawX + d.size / 2.0) > (enemy.drawX)) {
                        double dist = (d.drawX + d.size / 2.0) - (enemy.drawX + enemy.size / 2.0);
                        if (dist < closestDistance) {
                        	closestDistance = dist;
                            closest = d;
                        }
                    }
                }
               
                if (closest != null && closest.state != states.DIEING) {
                    double distance = (enemy.drawX ) - (closest.drawX);
                    if (distance < enemy.range && (closest.drawX) < (enemy.drawX )) {
                        enemy.attack(closest);
                    } else {
                        enemy.walk();
                    }
                } else {
                    enemy.walk();
                }
            }
        }

        //move projectiles, check collision
        Iterator<Projectile> projIt = projectiles.iterator();
        while (projIt.hasNext()) {
            Projectile p = projIt.next();
            p.update();

            if (p.x > WIDTH || p.x + p.width < 0) {
                projIt.remove();
                continue;
            }

            boolean hit = false;
            for (int row = 0; row < ROWS && !hit; row++) {
                double rowTop = GRID_START_Y + row * CELL_HEIGHT;
                double rowBottom = rowTop + CELL_HEIGHT;
                if (p.y + p.height > rowTop && p.y < rowBottom) {
                    for (Enemy e : enemies[row]) {
                        if (e.health <= 0) continue;
                        if (p.overlaps(e.drawX, e.drawY, e.size, e.size)) {
                            e.Hurt(p.damage);
                            hit = true;
                            break;
                        }
                    }
                }
            }
            if (hit) projIt.remove();
        }
        
        //defender attacks
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Defender d = grid[row][col];
                if (d == null) continue;
                d.currentCooldown -= delta;
                if (d.currentCooldown < 0) d.currentCooldown = 0;
                if (d.health <= 0 && d.state != states.DIEING && !d.toRemove) {
                    d.state = states.DIEING;
                    d.imgStep = 0;
                }
                if (d.toRemove) {
                    grid[row][col] = null;
                    continue;
                }


                Enemy target = null;
                for (Enemy e : enemies[row]) {
                    if (e.state == states.DIEING) continue;
                    double distance = (e.drawX + e.size / 2.0) - (d.drawX + d.size / 2.0);
                    if (distance < d.range && (d.drawX - d.size/2) < (e.drawX - e.size/2) && d.currentCooldown <= 0) {
                        target = e;
                        d.currentCooldown = d.attackCooldown;
                        break;
                    }
                }
                if (target != null) {
                    d.attack(target);
                }
            }
        }
        WaveProgress(now);
    }
    private void Gameover() {
    	if (gameOver) {
        	gc.setFill(Color.RED);
        	gc.setTextAlign(TextAlignment.CENTER);
        	gc.fillText("GAME OVER", WIDTH / 2.0, HEIGHT / 2.0);
        }
    }
    private void Victory() {
    	if (allWavesComplete) {
        	gc.setFill(Color.BLUE);
        	gc.setTextAlign(TextAlignment.CENTER);
        	gc.fillText("VICTORY!", WIDTH / 2.0, HEIGHT / 2.0);
        }
    }

    private void render() {
    	//Draw back ground
        gc.drawImage(background, 0, 0, WIDTH, HEIGHT);
        
        for(ArrayList<Enemy> rowEnemies : enemies) {
            for(Enemy enemy : rowEnemies) {
                enemy.draw(gc);
            }
        }
        // Draw defenders
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Defender d = grid[row][col];
                if (d != null) {      
                	d.draw(gc);
                }
            }
        }
        

            for (Projectile p : projectiles) {
                if (p.image != null) {
                    gc.drawImage(p.image, p.x, p.y, p.width, p.height);
                } else {
                    gc.fillRect(p.x, p.y, p.width, p.height);
                }
            }

        // Draw ghost preview
        if (selectedDefense != null) {
            int[] cell = getGridCell(dragX, dragY);
            Defender ghost = selectedDefense.copy(cell[0], cell[1]);
            if (cell != null && canPlaceDefender(cell[0], cell[1], selectedDefense)) {

            	gc.setGlobalAlpha(0.5);
            	ghost.draw(gc);
                gc.setGlobalAlpha(1);
            }
        }

        gc.drawImage(goldImg, 20, 0, 50, 60);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(24));
        gc.fillText(" " + gold, 50, 40);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(24));
        gc.fillText("Health : " + baseHealth,WIDTH - 150, 40);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(24));
        gc.fillText("Wave " + currentWave + " / " + WAVES, WIDTH / 2 , 40);
        
        gc.setFont(Font.font(50));
        
       Gameover();
       Victory();
        

    }

    private void drawGrid() {
    	
    	gc.setGlobalAlpha(1);
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);

        for (int row = 0; row <= ROWS; row++) {
            gc.strokeLine(
                    GRID_START_X,
                    GRID_START_Y + row * CELL_HEIGHT,
                    GRID_START_X + COLS * CELL_WIDTH,
                    GRID_START_Y + row * CELL_HEIGHT
            );
        }

        for (int col = 0; col <= COLS; col++) {
            gc.strokeLine(
                    GRID_START_X + col * CELL_WIDTH,
                    GRID_START_Y,
                    GRID_START_X + col * CELL_WIDTH,
                    GRID_START_Y + ROWS * CELL_HEIGHT
            );
        }
        gc.setGlobalAlpha(1);
    }


    class DefenseMenu extends HBox {

        private static final int ICON_SIZE = 60;
        private final List<DefenseIcon> icons = new ArrayList<>();

        DefenseMenu() {
            setPrefSize(1280, 70);
            setSpacing(15);
            setPadding(new Insets(5, 20, 5, 20));
            setAlignment(Pos.CENTER_LEFT);


            setBackground(new Background(
                    new BackgroundFill(Color.rgb(0, 0, 0, 0.8),
                            CornerRadii.EMPTY,
                            Insets.EMPTY)
            ));


            addDefender(new Knight(), "Knight", "file:sprites/knight/Knight icon.png");
            addDefender(new Archer(), "Archer", "file:sprites/Archer/icon.png");
            addDefender(new cannon(), "cannon", "file:sprites/Cañon_quieto.png");
        }

        private void addDefender(Defender type, String label, String imgPath) {
            DefenseIcon icon = new DefenseIcon(type, label, imgPath);
            icons.add(icon);
            getChildren().add(icon);
        }


        class DefenseIcon extends StackPane {

            DefenseIcon(Defender type, String labelText, String imgPath) {

                setPrefSize(ICON_SIZE, ICON_SIZE);


                setBorder(new Border(
                        new BorderStroke(Color.WHITE,
                                BorderStrokeStyle.SOLID,
                                new CornerRadii(10),
                                new BorderWidths(2))
                ));


                setBackground(new Background(
                        new BackgroundFill(Color.rgb(50, 50, 50, 0.5),
                                new CornerRadii(10),
                                Insets.EMPTY)
                ));


                Image img = new Image(imgPath);
                ImageView view = new ImageView(img);
                view.setFitWidth(ICON_SIZE - 10);
                view.setFitHeight(ICON_SIZE - 10);
                view.setPreserveRatio(true);


                Label txt = new Label(labelText);
                txt.setTextFill(Color.WHITE);
                txt.setFont(Font.font(10));
                txt.setAlignment(Pos.BOTTOM_CENTER);


                getChildren().addAll(view, txt);
                StackPane.setAlignment(txt, Pos.BOTTOM_CENTER);


                setOnMousePressed(e -> {
                    startDrag(type, e.getX(), e.getY());
                    e.consume();
                });
            }
        }
    }
    
    public static void addProjectile(Projectile p) {
        projectiles.add(p);
    }
        
    public static void main(String[] args) {
        launch(args);
    }
}