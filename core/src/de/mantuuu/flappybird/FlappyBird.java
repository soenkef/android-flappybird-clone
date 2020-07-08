package de.mantuuu.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	SpriteBatch batch;
	Texture background;
	ShapeRenderer shapeRenderer;

	Texture[] birds;
	BitmapFont font;

	Texture gameOver;

	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	float gravity = 2;
	int gameState = 0;
	Texture toptube;
	Texture bottomtube;
	float gap = 400;
	float maxTubeOffset;
	Random randomGenerator;
	int numberOfTubes = 4;

	float[] tubeOffset = new float[numberOfTubes];
	float[] tubeX = new float[numberOfTubes];
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	float tubeVelocity = 4;
	float distanceBetweenTubes;

	Circle birdCircle;

	int score;
	int scoringTube;

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		gameOver = new Texture("gameover.png");

		shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		toptube = new Texture("toptube.png");
		bottomtube = new Texture("bottomtube.png");

		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;

		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		startGame();

		score = 0;
		scoringTube = 0;

	}

	public void startGame() {
		score = 0;
		velocity = -23;
		birdY = Gdx.graphics.getHeight()/2 - birds[flapState].getHeight()/2;

		for (int i = 0; i < numberOfTubes; i++) {

			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2 - toptube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {

			if(Gdx.input.justTouched()) {

				velocity = -23;

			}

			for (int i = 0; i < numberOfTubes; i++) {

				if (tubeX[i] < - toptube.getWidth()) {

					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
					tubeX[i] += numberOfTubes * distanceBetweenTubes;

				} else {

					tubeX[i] = tubeX[i] - tubeVelocity;

					if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
						score++;

						Gdx.app.log("Score", String.valueOf(score));

						if (scoringTube < numberOfTubes - 1) {
							scoringTube++;
						} else {
							scoringTube = 0;
						}
					}

				}

				batch.draw(toptube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomtube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], toptube.getWidth(), toptube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOffset[i], bottomtube.getWidth(), bottomtube.getHeight());
			}

			if (birdY > 0) {

				velocity = velocity + gravity;
				birdY -= velocity;

			} else {

				gameState = 2;

			}

		} else if (gameState == 0) {

			if(Gdx.input.justTouched()) {
				gameState = 1;
			}

		} else if (gameState == 2) {

			batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);

			if(Gdx.input.justTouched()) {

				gameState = 1;
				startGame();
				scoringTube = 0;

			}

		}

		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}


		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);

		font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 7, Gdx.graphics.getHeight() / 6);

		batch.end();

		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

		// Test mit farbigen Formen - für die Kollisionserkennung
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i = 0; i < numberOfTubes; i++) {

			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], toptube.getWidth(), toptube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOffset[i], bottomtube.getWidth(), bottomtube.getHeight());

			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

				//Gdx.app.log("Collision", "Yep");
				gameState = 2;

			}

		}

		shapeRenderer.end();

	}
//
//	@Override
//	public void dispose () {
//		batch.dispose();
//		background.dispose();
//	}
}
