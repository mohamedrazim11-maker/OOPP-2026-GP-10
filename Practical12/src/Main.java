import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private JRadioButton englishRadio;
    private JRadioButton metricRadio;

    private JTextField weightField;
    private JTextField heightField;

    private JLabel weightUnitLabel;
    private JLabel heightUnitLabel;

    private JLabel bmiResultLabel;
    private JLabel categoryResultLabel;

    private JButton calculateButton;
    private JButton clearButton;

    public Main() {
        setTitle("BMI Calculator");

        setSize(500, 650);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //title//

        JLabel titleLabel = new JLabel("BMI Calculator");

        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));

        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        mainPanel.add(titleLabel, gbc);

        //unit label

        JLabel unitLabel = new JLabel("select unit");
        unitLabel.setFont(new Font("Arial", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;

        mainPanel.add(unitLabel, gbc);

        //radiobutton

        englishRadio = new JRadioButton("English");
        metricRadio = new JRadioButton("Metric");
        englishRadio.setSelected(true);

        ButtonGroup unitGroup = new ButtonGroup();
        unitGroup.add(englishRadio);
        unitGroup.add(metricRadio);

        JPanel radioPanel = new JPanel();
        radioPanel.add(englishRadio);
        radioPanel.add(metricRadio);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        mainPanel.add(radioPanel, gbc);

        //weight label

        JLabel weightLabel = new JLabel("weight");
        weightLabel.setFont(new Font("Arial", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;

        mainPanel.add(weightLabel, gbc);

        //weightField

        weightField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(weightField, gbc);

        //weight unit

        weightUnitLabel = new JLabel("weight in pounds(lb)");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;

        mainPanel.add(weightUnitLabel, gbc);

        //height label

        JLabel heightLabel = new JLabel("height");
        heightLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;

        mainPanel.add(heightLabel, gbc);

        //height field

        heightField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 5;
        mainPanel.add(heightField, gbc);

        //height unit

        heightUnitLabel = new JLabel("height in inches(in)");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        mainPanel.add(heightUnitLabel, gbc);

        //calculate button

        calculateButton = new JButton("Calculate BMI");
        calculateButton.setFont(new Font("Arial", Font.BOLD, 15));

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        mainPanel.add(calculateButton, gbc);

        //clear button

        clearButton = new JButton("Clear");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        mainPanel.add(clearButton, gbc);

        //BMI result

        bmiResultLabel = new JLabel("Your BMI:");
        bmiResultLabel.setFont(new Font("Arial", Font.BOLD, 20));

        bmiResultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        mainPanel.add(bmiResultLabel, gbc);

        //category result

        categoryResultLabel = new JLabel("Category:-");
        categoryResultLabel.setFont(new Font("Arial", Font.BOLD, 18));

        categoryResultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        mainPanel.add(categoryResultLabel, gbc);

        //BMI values title

        JLabel bmiValuesTitle = new JLabel("BMI VALUES");
        bmiValuesTitle.setFont(new Font("Arial", Font.BOLD, 18));

        bmiValuesTitle.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 11;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        mainPanel.add(bmiValuesTitle, gbc);

        //BMI values

        JLabel bmiValues = new JLabel(
                "<html>" + "Underwight: Less than 18.5 <br><br>" +
                        "Normal:18.5-24.9 <br><br>" +
                        "Overweight:25-29.9 <br><br>" +
                        "Obese:30 or greater" + "<html>"
        );

        bmiValues.setFont(new Font("Arial", Font.PLAIN, 15));

        gbc.gridy = 12;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        mainPanel.add(bmiValues, gbc);

        add(mainPanel);

        //english radio button

        englishRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                weightUnitLabel.setText("Weight in pounds(lb)");
                heightUnitLabel.setText("height in inches(in)");


            }
        });

        //metric radio button

        metricRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                weightUnitLabel.setText("Weight in Kilograms(kg)");
                heightUnitLabel.setText("height in meters(m)");


            }
        });

        //calculate button

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateBMI();
            }
        });

        //clear button

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFiels();
            }
        });

    }


        //BMI calculate method

        private void calculateBMI() {
            String weightText = weightField.getText().trim();
            String heightText = heightField.getText().trim();

            if(weightText.isEmpty() || heightText.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Please enter weight and height.");

                return;
            }
            try {
                double weight = Double.parseDouble(weightText);
                double height = Double.parseDouble(heightText);

                if (weight <= 0 || height <= 0) {
                    JOptionPane.showMessageDialog(this, "weight and height must be greater than 0");

                    return;

                }
                double bmi;

                //english formula

                if (englishRadio.isSelected()) {
                    bmi = (weight * 703) / (height * height);

                }
                //metric formula

                else {
                    bmi = weight / (height * height);

                }
                String category;

                if (bmi < 18.5) {
                    category = "Underweight";
                } else if (bmi < 25) {
                    category = "Normal";
                } else if (bmi < 30) {
                    category = "Overweight";
                } else {
                    category = "Obese";
                }

                bmiResultLabel.setText(String.format("Your BMI:%.2f", bmi));

                categoryResultLabel.setText("Category:" + category);
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(this,"Please enter valid numbers");

            }

        }

    //clear method

    private void clearFiels(){
         weightField.setText("");
         heightField.setText("");
         bmiResultLabel.setText("Your BMI:-");

         categoryResultLabel.setText("Category:-");

         englishRadio.setSelected(true);
         weightUnitLabel.setText("weight in pounds(lb)");
         heightUnitLabel.setText("height in inches(in)");
    }

    //main method

    public static void main(String[] args){
         SwingUtilities.invokeLater(
                 new Runnable() {
                     @Override
                     public void run() {
                         new Main().setVisible(true);
                     }
                 }
         );
    }

}