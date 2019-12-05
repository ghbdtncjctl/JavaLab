package obba2;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.*;
import javax.imageio.ImageIO;
import javax.swing.*;
@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private static int WIDTH=600;
	private static int HEIGHT=480;
	
	private JTextField textFieldX;
	private JTextField textFieldY;
	private JTextField textFieldZ;
	
	private JLabel textFieldmem1;
	private JLabel textFieldmem2;
	private JLabel textFieldmem3;
	private JLabel textFieldResult;
	
	private ImagePanel imagePane;
	private ButtonGroup radioButtonsFormula = new ButtonGroup();
	private ButtonGroup radioButtonsPeremen = new ButtonGroup();
	
	
	private Box hboxImageFormula = Box.createHorizontalBox();
	private Box hboxFormulaType = Box.createHorizontalBox();
	private int formulaId = 1;
	private double mem1=0;
	private double mem2=0;
	private double mem3=0;
	private int PeremenId=1;

	public Double calculate1(Double x, Double y,Double z) {
	return Math.sin(Math.log(y)+Math.sin(Math.PI*y*y))*Math.sqrt(Math.sqrt(x*x+Math.sin(z)+ Math.exp(Math.cos(z))));
	}

	public Double calculate2(Double x, Double y,Double z) {
		Double b1=Math.cos(Math.exp(x));
		Double b2=Math.log(Math.pow(1+y, 2));
		Double a0=Math.exp(Math.cos(x));
		Double a1=Math.sin(Math.PI*z)*Math.sin(Math.PI*z);
		Double b3=Math.sqrt(a0+a1);
		Double b4=Math.sqrt(Math.pow(x,-1));
		Double b5=Math.cos(y*y);
		Double b=b1+b2+b3+b4+b5;
		return Math.pow(b,Math.sin(z));
	}

	private void addRadioButtonFormula(String buttonName, final int formulaId, final BufferedImage image)  {
	JRadioButton button = new JRadioButton(buttonName);
	button.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent ev) {
			MainFrame.this.formulaId = formulaId;
			imagePane=new ImagePanel(image,2);
			hboxImageFormula.removeAll();
			hboxImageFormula.add(imagePane);
			imagePane.updateUI();
	}
	});
	radioButtonsFormula.add(button);
	hboxFormulaType.add(button);
	}
	private void addRadioButtonPeremen(String buttonName, final int PeremenId,Box box)  {
		JRadioButton button = new JRadioButton(buttonName);
		button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			MainFrame.this.PeremenId=PeremenId;
		}
		});
		radioButtonsPeremen.add(button);
		box.add(button);
		}
	// Конструктор класса
	public MainFrame() throws IOException {
		
	super("Вычисление формулы");
	setSize(WIDTH, HEIGHT);
	Toolkit kit = Toolkit.getDefaultToolkit();
	setLocation((kit.getScreenSize().width - WIDTH)/2,
	(kit.getScreenSize().height - HEIGHT)/2);
	
	hboxFormulaType.add(Box.createHorizontalGlue());
	System.out.println(new File("f1.jpg").exists());
	System.out.println(new File("f2.jpg").exists());
	BufferedImage image1 = ImageIO.read(new java.io.File("f1.jpg"));
	BufferedImage image2 = ImageIO.read(new java.io.File("f2.jpg"));
	addRadioButtonFormula("Формула 1", 1,image1);
	addRadioButtonFormula("Формула 2", 2,image2);
	radioButtonsFormula.setSelected(radioButtonsFormula.getElements().nextElement().getModel(), true);
	imagePane = new ImagePanel(image1,2);
	hboxFormulaType.add(Box.createHorizontalGlue());
	hboxFormulaType.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
	hboxImageFormula.add(imagePane);
	// Создать область с полями ввода для X и Y
	
	JLabel labelForX = new JLabel("X:");
	textFieldX = new JTextField("0", 10);
	textFieldX.setMaximumSize(textFieldX.getPreferredSize());
	JLabel labelForY = new JLabel("Y:");
	textFieldY = new JTextField("0", 10);
	textFieldY.setMaximumSize(textFieldY.getPreferredSize());
	JLabel labelForZ = new JLabel("Z:");
	textFieldZ = new JTextField("0", 10);
	textFieldZ.setMaximumSize(textFieldZ.getPreferredSize());
	
	
	Box hboxVariables = Box.createHorizontalBox();
	hboxVariables.setBorder(
	BorderFactory.createLineBorder(Color.RED));
	hboxVariables.add(Box.createHorizontalGlue());
	hboxVariables.add(labelForX);
	hboxVariables.add(Box.createHorizontalStrut(10));
	hboxVariables.add(textFieldX);
	hboxVariables.add(Box.createHorizontalGlue());
	hboxVariables.add(labelForY);
	hboxVariables.add(Box.createHorizontalStrut(10));
	hboxVariables.add(textFieldY);
	hboxVariables.add(Box.createHorizontalGlue());
	hboxVariables.add(labelForZ);
	hboxVariables.add(Box.createHorizontalStrut(10));
	hboxVariables.add(textFieldZ);
	hboxVariables.add(Box.createHorizontalGlue());
	// Создать область для вывода результата
	
	JLabel labelForResult = new JLabel("Результат:");
	//labelResult = new JLabel("0");
	textFieldResult = new JLabel("0", 10);
	textFieldResult.setMaximumSize(
	textFieldResult.getPreferredSize());
	Box hboxResult = Box.createHorizontalBox();
	hboxResult.add(Box.createHorizontalGlue());
	hboxResult.add(labelForResult);
	hboxResult.add(Box.createHorizontalStrut(10));
	hboxResult.add(textFieldResult);
	hboxResult.add(Box.createHorizontalGlue());
	hboxResult.setBorder(BorderFactory.createLineBorder(Color.BLUE));
	
	Box hboxPeremen=Box.createVerticalBox();
	Box hboxPeremen1=Box.createHorizontalBox();
	Box hboxPeremen2=Box.createHorizontalBox();
	Box hboxPeremen3=Box.createHorizontalBox();
	
	hboxPeremen1.add(Box.createHorizontalGlue());
	hboxPeremen2.add(Box.createHorizontalGlue());
	hboxPeremen3.add(Box.createHorizontalGlue());
	
	addRadioButtonPeremen("X:",1,hboxPeremen1);
	addRadioButtonPeremen("Y:",2,hboxPeremen2);
	addRadioButtonPeremen("Z:",3,hboxPeremen3);
	
	radioButtonsPeremen.setSelected(radioButtonsPeremen.getElements().nextElement().getModel(), true);
	
	textFieldmem1 = new JLabel("0");
	textFieldmem2 = new JLabel("0");
	textFieldmem3 = new JLabel("0");
	
	hboxPeremen1.add(Box.createHorizontalStrut(10));
	hboxPeremen1.add(textFieldmem1);
	hboxPeremen1.add(Box.createHorizontalGlue());
	
	hboxPeremen2.add(Box.createHorizontalStrut(10));
	hboxPeremen2.add(textFieldmem2);
	hboxPeremen2.add(Box.createHorizontalGlue());
	
	hboxPeremen3.add(Box.createHorizontalStrut(10));
	hboxPeremen3.add(textFieldmem3);
	hboxPeremen3.add(Box.createHorizontalGlue());
	
	hboxPeremen.add(Box.createVerticalGlue());
	hboxPeremen.add(hboxPeremen1);
	hboxPeremen.add(Box.createVerticalGlue());
	hboxPeremen.add(hboxPeremen2);
	hboxPeremen.add(Box.createVerticalGlue());
	hboxPeremen.add(hboxPeremen3);
	hboxPeremen.add(Box.createVerticalGlue());
	
	
	// Создать область для кнопок
	JButton buttonCalc = new JButton("Вычислить");
	buttonCalc.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent ev) {
	try {
	 Double x = Double.parseDouble(textFieldX.getText());
	 Double y = Double.parseDouble(textFieldY.getText());
	 Double z = Double.parseDouble(textFieldZ.getText());
	 textFieldmem1.setText(x.toString());
	 textFieldmem2.setText(y.toString());
	 textFieldmem3.setText(z.toString());
	 mem1=x;
	 mem2=y;
	 mem3=z;
	 Double result;
	 if (formulaId==1)
	result = calculate1(x, y,z);
	 else
	result = calculate2(x, y,z);
	 textFieldResult.setText(result.toString());
		} 
	catch (NumberFormatException ex) {
	JOptionPane.showMessageDialog(MainFrame.this,"Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа",JOptionPane.WARNING_MESSAGE);
				}
		}
	});
	JButton buttonReset = new JButton("Очистить поля");
	buttonReset.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent ev) {
	textFieldX.setText("0");
	textFieldY.setText("0");
	textFieldZ.setText("0");
	textFieldResult.setText("0");
	}
	});
	
	Box hboxButtonsCaRe = Box.createHorizontalBox();
	hboxButtonsCaRe.add(Box.createHorizontalGlue());
	hboxButtonsCaRe.add(buttonCalc);
	hboxButtonsCaRe.add(Box.createHorizontalStrut(30));
	hboxButtonsCaRe.add(buttonReset);
	hboxButtonsCaRe.add(Box.createHorizontalGlue());
	hboxButtonsCaRe.setBorder(
	BorderFactory.createLineBorder(Color.GREEN));
	
	JButton buttonM = new JButton("M+");
	buttonM.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent ev) {
		try {
			Double result=Double.parseDouble(textFieldResult.getText());
			 if (PeremenId==1)
			{
				 mem1+=result;
				 Double mem=mem1;
				 textFieldmem1.setText(mem.toString());
				 textFieldResult.setText(mem.toString());
			}
			 else if(PeremenId==2)
			 {
				 mem2+=result;
				 Double mem=mem2;
				 textFieldmem2.setText(mem.toString());
				 textFieldResult.setText(mem.toString());
			 }
			 else 
			 {
				 mem3+=result;
				 Double mem=mem2;
				 textFieldmem3.setText(mem.toString());
				 textFieldResult.setText(mem.toString());
			 }
				} 
			catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(MainFrame.this,"Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа",JOptionPane.WARNING_MESSAGE);
						}
	}
	});
	
	JButton buttonMC = new JButton("MC");
	buttonMC.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent ev) {
	if(PeremenId==1){
		mem1=0;
		textFieldmem1.setText("0");}
	else if(PeremenId==2){
		mem2=0;
		textFieldmem2.setText("0");}
	else {
		mem3=0;
		textFieldmem3.setText("0");}
	
	}
	});
	
	Box hboxButtonsMcM = Box.createHorizontalBox();
	hboxButtonsMcM.add(Box.createHorizontalGlue());
	hboxButtonsMcM.add(buttonMC);
	hboxButtonsMcM.add(Box.createHorizontalGlue());
	hboxButtonsMcM.add(buttonM);
	hboxButtonsMcM.add(Box.createHorizontalGlue());
	hboxButtonsMcM.setBorder(
	BorderFactory.createLineBorder(Color.GREEN));
	
	// Связать области воедино в компоновке BoxLayout
	Box contentBox = Box.createVerticalBox();
	contentBox.add(Box.createVerticalGlue());
	contentBox.add(hboxFormulaType);
	contentBox.add(Box.createVerticalGlue());
	contentBox.add(hboxImageFormula);
	contentBox.add(Box.createVerticalGlue());
	contentBox.add(hboxVariables);
	contentBox.add(hboxResult);
	contentBox.add(hboxPeremen);
	contentBox.add(hboxButtonsCaRe);
	contentBox.add(hboxButtonsMcM);
	contentBox.add(Box.createVerticalGlue());
	getContentPane().add(contentBox, BorderLayout.CENTER);
	}
	// Главный метод класса

	public static void main(String[] args) throws IOException {
		MainFrame frame=new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
