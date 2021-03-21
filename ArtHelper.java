import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.*;

public class ArtHelper
{
	private BufferedImage originalImage;
	private int width;
	private int height;
	private int[] pixelArray;
	private int[] progressArray;
	private int[] valueScale;

	// The "window" for all components to display.
	private static JFrame frame;
	private JLabel label;
	private ImageIcon icon;

	public ArtHelper(BufferedImage originalImage)
	{
		this.originalImage = originalImage;
		this.width = originalImage.getWidth();
		this.height = originalImage.getHeight();
		this.pixelArray = new int[this.height * this.width];

		this.progressArray = new int[this.height * this.width];

		this.frame = new JFrame("Display");
		this.frame.setSize(1100, 700);
		this.label = new JLabel();

	}

	public void displayImage(BufferedImage img, String side)
	{
		// Displays the image on the specified side of the screen.
		this.icon = new ImageIcon(img); // Creates an image icon.
		this.label.setIcon(icon); // Puts the icon on a label.
		frame.getContentPane().add(label, side); // Puts the label on the left side of the frame.
		frame.setVisible(true); // Displays the frame.
		frame.repaint();
	}

	public void fillArray()
	{
		int index = 0;
		for (int row = 0; row < height; row++)
		{
			for (int col = 0; col < width; col++, index++)
			{
				final Color temp = new Color(this.originalImage.getRGB(col, row));
				this.pixelArray[index] = temp.getRed();
			}
		}
	}

	public void setValueScale(int numValues)
	{
		this.valueScale = new int[numValues];
		this.valueScale[0] = 0;
		for (int i = 1; i < numValues; i++)
		{
			this.valueScale[i] = this.valueScale[i - 1] + (int) (255 / (numValues - 1));
		}

	}

	public BufferedImage changeArray(int step)
	{
		for (int i = 0; i < this.progressArray.length; i++)
			this.progressArray[i] = -1;
	
		int range = (int) ((this.valueScale[1] - this.valueScale[0]) / 2);

		for (int i = 0; i < this.pixelArray.length; i++)
		{
			for (int j = 0; j < step; j++)
			{
				int compareVal = this.valueScale[j];
				if ((this.pixelArray[i] >= compareVal - range) && (this.pixelArray[i] <= compareVal + range))
				{
					// Using a logical OR to stick together the red, green and blue values
					// in the correct positions into a single int.
					// Red, green and blue values should all be the same
					// because the image should already be greyscale.
					this.progressArray[i] = (int) compareVal << 16 | (int) compareVal << 8 | (int) compareVal;
				}
			}
			if (this.progressArray[i] == -1)
				this.progressArray[i] = (int) 255 << 16 | (int) 255 << 8 | (int) 255;
		}

		// Create an image from the array and return the image.
		BufferedImage changedImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);    
  		changedImage.setRGB(0, 0, this.width, this.height, this.progressArray, 0, this.width);
		return changedImage;
	}

	
	public static void main(String... args)
	{
		BufferedImage originalImage = null;

		try // loading images
		{
			originalImage = ImageIO.read(new File("reference.jpeg"));
		}
		catch (IOException ex)
		{
			System.out.println("The image does not exist. " + ex);
			System.exit(1);
		}
	
		ArtHelper reference = new ArtHelper(originalImage); // Create an object with the original photo.

		// Get user input for number of values.
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the number of values you want in the final: ");
		int numVal = scan.nextInt();
		reference.setValueScale(numVal + 1);

		// Before changes.
		System.out.println("Original reference photo");
		ImageIcon refIcon = new ImageIcon(originalImage);
		JLabel refLabel = new JLabel();
		refLabel.setIcon(refIcon);
		frame.getContentPane().add(refLabel, BorderLayout.WEST);
		frame.setVisible(true);

		reference.fillArray(); // Fill the pixel array with values using the original photo.

		// Change the image in steps.
		for (int step = 1; step < numVal; step++)
		{
			System.out.println("Step " + step);
		
			// After changes.
			BufferedImage changedImage = reference.changeArray(step); // Change the array and store the changed image.
			reference.displayImage(changedImage, BorderLayout.EAST); // Display the changed image on the right.

			System.out.println("Type 'y' to move to the next step.");
			scan.next();
		}
		System.out.println("You're done!");
	}
}
