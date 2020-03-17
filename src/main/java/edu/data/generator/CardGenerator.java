package edu.data.generator;

import edu.data.generator.config.Config;
import edu.data.generator.model.BoundingBox;
import edu.data.generator.model.GeneratedData;
import edu.data.generator.util.ImageUtil;
import edu.data.generator.util.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
// import java.awt.geom.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.lang.Math.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;


import static edu.data.generator.config.Config.COLS;
import static edu.data.generator.config.Config.DEBUG;
import static edu.data.generator.config.Config.IMAGE_HEIGHT;
import static edu.data.generator.config.Config.IMAGE_WIDTH;
import static edu.data.generator.config.Config.MARGIN_AROUND;
import static edu.data.generator.config.Config.MARGIN_BETWEEN;
import static edu.data.generator.config.Config.MAX_ANGLE_TO_ROTATE;
import static edu.data.generator.config.Config.ROWS;

import javax.swing.*;

public class CardGenerator {
    private final static Logger LOG = LoggerFactory.getLogger(CardGenerator.class);
    private final List<BufferedImage> images;
    private final List<String> classNames;
    private final Random random;
    private float cardSize;
    private boolean initialized = false;

    public CardGenerator() {
        images = new ArrayList();
        classNames = new ArrayList();
        random = new Random();
    }

    public void init() {
        readImages();
        initialized = true;
    }

    public GeneratedData putCardsToBackground(final BufferedImage image, final double scale) {
        if (!initialized) {
            init();
        }
        return copyCardsToBg(getRandomCards(), image, scale);
    }

    public List<String> getClassNames() {
        return classNames;
    }
    
    private GeneratedData copyCardsToBg(final List<Integer> cardIndexes, final BufferedImage image, final double grainScale) {
        // int index = 0;
        final Graphics2D graphics = (Graphics2D) image.getGraphics();
        final List<BoundingBox> boxes = new ArrayList();
        final int bgWidth = image.getWidth();
        final int bgHeight = image.getHeight();
        int index;
        // for (Iterator<Integer> iter = cardIndexes.iterator(); iter.hasNext(); ){
        for (index = 0; index < cardIndexes.size(); index++){
            // index = iter.next();
            final int angle = random.nextInt(Config.MAX_ANGLE_TO_ROTATE);
            final int cardIndex = cardIndexes.get(index);
            
            BufferedImage seedImage = images.get(cardIndex);
            final int sIW = seedImage.getWidth();
            final int sIH = seedImage.getHeight();
            final int seedW = Math.min(sIW, sIH);
            final int seedL = Math.max(sIW, sIH);
            final double seedAR = (double) seedW/seedL;
            
            final String seedClass = classNames.get(cardIndex).split("-")[0];
            double seedScale = 0.0;
            if (seedClass.equals("BS")){
                seedScale = (grainScale/1.75) / seedW;
            } else if(seedClass.equals("BSSingle")){
                seedScale = (grainScale/1.5) / seedW;
            } else if(seedClass.equals("BSPair")){
                if(seedAR <= 0.66){
                    seedScale = (double) (grainScale/1.25 / seedW);
                } else {
                    seedScale = (double) (grainScale/1.75 / seedW);
                }
            }
            final int newWidth =  (int) (sIW * seedScale);
            final int newHeight = (int) (sIH * seedScale);
            
            seedImage = ImageUtil.progressiveScaleImage(seedImage, newWidth, newHeight);
            // final BufferedImage rotatedCard = rotateCard(ImageUtil.cloneImage(seedImage, true), angle ); 
            final BufferedImage rotatedCard = rotateCard(ImageUtil.cloneImage(seedImage, true), 0); //TEST!!!!!!);


            final int posX = Config.MARGIN_AROUND + random.nextInt(bgWidth - Config.MARGIN_AROUND);
            final int posY = Config.MARGIN_AROUND + random.nextInt(bgHeight - Config.MARGIN_AROUND);
            
            LOG.info("Spawning {}[{}°] at {}% from left, {}% from top", classNames.get(cardIndex), angle, Math.round( (double) posX/bgWidth*10000 )/100.0, Math.round( (double) posY/bgHeight*10000 )/100.0);

            if (Config.BLUR) {
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }

            graphics.drawImage(rotatedCard, posX, posY, null);
            final BoundingBox bBox = createBoundingBox(posX, posY, rotatedCard.getWidth(), rotatedCard.getHeight(), cardIndex);
            if (DEBUG) {
                showBoundingBoxes(graphics, bBox, cardIndex);
            }

            boxes.add(bBox);
            // index++;
        }



        return new GeneratedData(image,boxes);
    }
    private void draw(BufferedImage img){
        JFrame frame = new JFrame("");
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        ImageIcon imageIcon = new ImageIcon(img);
        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);
        frame.getContentPane().add(jLabel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // private GeneratedData copyCardsToBackground(final List<Integer> cardIndexes, final BufferedImage image) {
    //     int index= 0;
    //     final int offset = (int) cardSize + MARGIN_BETWEEN;
    //     final Graphics2D graphics = (Graphics2D) image.getGraphics();
    //     final List<BoundingBox> boxes = new ArrayList();

    //     final int baseOffsetX = (int) (IMAGE_WIDTH - (COLS * cardSize + (COLS - 1) * MARGIN_BETWEEN)) / 2;
    //     final int baseOffsetY = (int) (IMAGE_HEIGHT - (ROWS * cardSize + (ROWS - 1) * MARGIN_BETWEEN)) / 2;

    //     for (int y=0; y<ROWS; y++) {
    //         for (int x=0; x<COLS; x++) {

    //             final int angle = random.nextInt(2 * MAX_ANGLE_TO_ROTATE) - MAX_ANGLE_TO_ROTATE;
    //             final int cardIndex = cardIndexes.get(index);
    //             final BufferedImage rotatedCard = rotateCard(ImageUtil.cloneImage(images.get(cardIndex), true), Math.toRadians(angle));

    //             final int posX = baseOffsetX + x * offset;
    //             final int posY = baseOffsetY + y * offset;

    //             if (Config.BLUR) {
    //                 graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    //             }
    //             graphics.drawImage(rotatedCard, posX, posY, null);
    //             final BoundingBox bBox = createBoundingBox(posX, posY, rotatedCard.getWidth(), rotatedCard.getHeight(), cardIndex);

    //             if (DEBUG) {
    //                 showBoundingBoxes(graphics, bBox, cardIndex);
    //             }

    //             boxes.add(bBox);
    //             index++;
    //         }
    //     }

    //     graphics.dispose();
    //     return new GeneratedData(image, boxes);
    // }

    private BoundingBox createBoundingBox(final int posX, final int posY, final int width, final int height, final int index) {
        final BoundingBox bBox = new BoundingBox();
        bBox.setxMin(posX);
        bBox.setyMin(posY);
        bBox.setxMax(posX + width);
        bBox.setyMax(posY + height);
        bBox.setClassName(classNames.get(index).split("-")[0]);
        return bBox;
    }

    private void showBoundingBoxes(final Graphics2D graphics, final BoundingBox bBox, final int index) {
        graphics.setColor(Color.GREEN);
        graphics.drawRect(bBox.getxMin(), bBox.getyMin(), bBox.getxMax() - bBox.getxMin(), bBox.getyMax() - bBox.getyMin());
        graphics.drawString(bBox.getClassName(), bBox.getxMin(), bBox.getyMin() - 5);
    }

    private BufferedImage rotateCard(final BufferedImage card, final Integer angle) {
        //Rotate seed
        
        int w = card.getWidth();
        int h = card.getHeight();
        double pivot_x = w / 2.0;
        double pivot_y = h / 2.0;
        // final AffineTransform tx = AffineTransform.getRotateInstance(angle, pivot_x, pivot_y);
        final AffineTransform tx = AffineTransform.getRotateInstance(angle, 0, 0);
        // AffineTransformOp op = new AffineTransformOp(tx, java.awt.image.AffineTransformOp.TYPE_BILINEAR);
        
        double th = angle % 90.0;
        double phi = Math.abs( 90 - th % 90 );
        th  = Math.toRadians(th);
        phi = Math.toRadians(phi);
        int newWidth =  (int) ( w * Math.cos( th ) + h * Math.cos( phi ) );
        int newHeight = (int) ( w * Math.sin( th ) + h * Math.sin( phi ) );

        // tx.translate(pivot_x, pivot_y);
        tx.rotate(th);
        
        // Rotate the card
        // final AffineTransform transform = new AffineTransform();

        // final Double offsetX = Math.sin(angle) * card.getHeight();
        // final Double offsetY = Math.sin(angle) * card.getWidth();

        // transform.translate((offsetX > 0) ? offsetX : 0, (offsetY < 0) ? - offsetY : 0);
        // transform.rotate(angle);

        // final int newWidth = (int) (card.getWidth() + Math.abs(offsetX));
        // final int newHeight = (int) (card.getHeight() + Math.abs(offsetY));

        // Rescale the card
        // final float largestSide = Math.max(newWidth, newHeight);
        // final double proportion = (cardSize + Math.abs(Math.sin(angle) * cardSize)) / largestSide;
        // final int rescaledWidth = (int) (newWidth * proportion);
        // final int rescaledHeight = (int) (newHeight * proportion);

        final BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D rotatedCardGraphics = (Graphics2D) rotatedImage.getGraphics();

        if (Config.BLUR) {
            rotatedCardGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        rotatedCardGraphics.drawImage(card, tx, null);
        rotatedCardGraphics.dispose();

        return rotatedImage;
    }

    private List<Integer> getRandomCards() {
        final List<Integer> cardIndexes = new ArrayList();
        final int cardCount = (int) ( Config.ROWS * Config.COLS * (0.6 + random.nextInt(10)/10.0)/2 ); //Generate at least half as many as Max
        for (int i = 0; i< cardCount; i++) {
            cardIndexes.add(random.nextInt(images.size()));
        }
        return cardIndexes;
    }

    private void readImages() {
        // cardSize = calcCardSize();
        // cardSize = 16;
        LOG.info("Loading, please wait. This process can take some time..., Card size: {}", cardSize);
        int i = 0;
        Path path = Paths.get(Config.SOURCE_DIR);
        long lineCnt = 0;
        lineCnt = path.toFile().listFiles().length;
        for (final File file : Reader.listFilesFromDir(Config.SOURCE_DIR)) {
            i++;
            double pc = Math.round((double)10000 * i/lineCnt)/100.0;
            final BufferedImage image = ImageUtil.readImage(file);
            // float largestSide = Math.max(image.getWidth(), image.getHeight());
            // float proportion = cardSize / largestSide;
            // int newWidth = (int) (image.getWidth() * proportion);
            // int newHeight = (int) (image.getHeight() * proportion);
            // images.add(ImageUtil.progressiveScaleImage(image, newWidth, newHeight));
            images.add(ImageUtil.progressiveScaleImage(image, 0, 0));
            // imageNames.add(getClassName(file.getName()));

            classNames.add(getClassName(file.getName()));
            System.out.printf("%s loaded - [%4.2f%%]\r", file.getName(), pc);
        }
        LOG.info("{} images to process.", images.size());
    }

    private String getClassName(final String fileName) {
        final String withoutExtension = fileName.substring(0, fileName.indexOf('.'));
        for (final String classLabel : Config.CLASS_LABELS) {
            if (withoutExtension.split("-")[0] == classLabel) {
                return classLabel;
            }
        }

        return withoutExtension;
    }

    private float calcCardSize() {
        final float cardWidth = (float) (IMAGE_WIDTH - (COLS-1) * MARGIN_BETWEEN - 2 * MARGIN_AROUND) / (float) COLS;
        final float cardHeight = (float) (IMAGE_HEIGHT - (ROWS-1) * MARGIN_BETWEEN - 2 * MARGIN_AROUND) / (float) ROWS;
        return Math.min(cardWidth, cardHeight);
    }
}
