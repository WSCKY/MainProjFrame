/*
 * @brief  file Cipher.
 * @author kyChu
 * @Date   2017/6/1
 */
package FileEncrypter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import FirmwareRule.FileEncrypter;
import FirmwareRule.FileHeader;
import FirmwareRule.FileNameRegular;

public class MainEncrypter extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final byte Major = 2;
	private static final byte Minor = 0;
	private static final byte FixNumber = 0;

	private JPanel NorthPanel = null;
	private JPanel SouthPanel = null;

	private JLabel src_info = null;
	private JTextField src_txt = null;

	private JButton EncryptBtn = null;
	private JButton OpenFileBtn = null;
	private JFileChooser FileChoose = null;

	private File srcFile = null;
	private String srcFilePath = null;
	private String dstFilePath = null;
	public MainEncrypter() {
		NorthPanel = new JPanel();
		SouthPanel = new JPanel();
		NorthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		src_info = new JLabel("src:");
		src_info.setFont(new Font("Courier New", Font.BOLD, 20));
		src_txt = new JTextField(40);
		src_txt.setFont(new Font("Courier New", Font.BOLD, 20));
		src_txt.setEditable(false);
		src_txt.setToolTipText("file path");

		OpenFileBtn = new JButton(" ... ");
		OpenFileBtn.addActionListener(obl);
		OpenFileBtn.setToolTipText("choose file");

		NorthPanel.add(src_info);
		NorthPanel.add(src_txt);
		NorthPanel.add(OpenFileBtn);

		new DropTarget(src_txt, DnDConstants.ACTION_COPY_OR_MOVE, txtl, true);

		EncryptBtn = new JButton("Encrypt");
		EncryptBtn.setFont(new Font("Courier New", Font.BOLD, 20));
		EncryptBtn.setPreferredSize(new Dimension(150, 30));
		EncryptBtn.addActionListener(ebl);
		EncryptBtn.setToolTipText("Encrypt");
		EncryptBtn.requestFocus();
		SouthPanel.add(EncryptBtn);

		add(NorthPanel, BorderLayout.NORTH);
		add(SouthPanel, BorderLayout.SOUTH);

		this.getRootPane().setDefaultButton(EncryptBtn);//enter key response.
		this.setTitle("kyChu.FileEncryptor V" + Major + "." + Minor + "." + FixNumber);
//		this.setLocation(670, 470);
		this.setSize(610, 106);
		this.setLocationRelativeTo(null);//center of screen.
		this.setIconImage(getToolkit().getImage(MainEncrypter.class.getResource("FileEncrypt.png")));
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private ActionListener ebl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(srcFile != null && srcFilePath != null && dstFilePath != null) {
				//step 1, write header.
				File dstFile = new File(dstFilePath);
				if(!dstFile.getParentFile().exists()) {
					dstFile.getParentFile().mkdirs();
				}
				try {
					dstFile.createNewFile();// it is okay.
					OutputStream fout = new FileOutputStream(dstFile, false);/* write to the beginning of file */
					String name = srcFile.getName();
					FileHeader header = new FileHeader(2333, FileNameRegular.getVersion(name), FileNameRegular.getType(name));
					fout.write(header.toBytes());
					fout.flush();
					fout.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				//step 2, encrypt.
				try {
					FileEncrypter.EncryptFile(srcFile, dstFile, true);//write to the end of the file.
					JOptionPane.showMessageDialog(null, "File Encrypt OK!\n" +
														"Size: " + dstFile.length() + " Bytes.\n" +
														(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())), "success", JOptionPane.INFORMATION_MESSAGE);
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "select a file first!", "error", JOptionPane.ERROR_MESSAGE);
			}
		}
	};

	private ActionListener obl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			FileSystemView fsv = FileSystemView.getFileSystemView();
			FileChoose = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("binary file(*.bin)", "bin");//("binary file(*.bin;*.txt)", "bin", "txt");
			FileChoose.setFileFilter(filter);
			Preferences pref = Preferences.userRoot().node(this.getClass().getName());
			String lastPath = pref.get("lastPath", "");//get it from System registry.
			if(!lastPath.equals("")) {
				File LastFilePath = new File(lastPath);
				if(LastFilePath.exists())
					FileChoose.setCurrentDirectory(new File(lastPath));
				else
					FileChoose.setCurrentDirectory(fsv.getHomeDirectory());
			} else
				FileChoose.setCurrentDirectory(fsv.getHomeDirectory());
//			FileChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int ret = FileChoose.showDialog(null, "Choose");
			if(ret == JFileChooser.APPROVE_OPTION ) {
				File file = FileChoose.getSelectedFile();
				pref.put("lastPath", file.getParent()); //Save it to System registry.
				String prefix = file.getName().substring(file.getName().lastIndexOf("."));
				if(prefix.equals(".bin")) {
					if(FileNameRegular.IsValidName(file.getName(), ".bin")) {
						srcFile = FileChoose.getSelectedFile();
						srcFilePath = srcFile.getPath();
						dstFilePath = srcFilePath.substring(0, srcFilePath.lastIndexOf(".")).concat(".pnx");
						src_txt.setText(srcFile.getPath());
//						System.out.println(file.getPath());
//						System.out.println(dstFile.toString());
					} else {
						JOptionPane.showMessageDialog(null, "file name error!", "error!", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "file type error!", "error!", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	};

	private DropTargetListener txtl = new DropTargetListener() {
		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			// TODO Auto-generated method stub
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			// TODO Auto-generated method stub
			try {
				if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);// important!
					@SuppressWarnings("unchecked")
					/* attention: should "import java.util.List;" (not awt.List) */
					List<File> list = (List<File>)(dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
					for(File file : list) {
						if(file.isFile()) {
							String prefix = file.getName().substring(file.getName().lastIndexOf("."));
//							System.out.println(prefix);
							if(prefix.equals(".bin")) {
								if(FileNameRegular.IsValidName(file.getName(), ".bin")) {
//									System.out.println(file.getPath());
									srcFile = file;
									srcFilePath = file.getPath();
									dstFilePath = srcFilePath.substring(0, srcFilePath.lastIndexOf(".")).concat(".pnx");
									src_txt.setText(file.getPath());
									break;
								} else {
									JOptionPane.showMessageDialog(null, "file name error!", "error!", JOptionPane.ERROR_MESSAGE);
								}
							}
						}
					}
					dtde.dropComplete(true);
				} else {
					dtde.rejectDrop();
				}
			} catch (UnsupportedFlavorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date Today = new Date();
		Date InvalidDay;
		try {
			InvalidDay = df.parse("2018-6-6");
			if(Today.getTime() > InvalidDay.getTime()) {
				JOptionPane.showMessageDialog(null, "±§Ç¸£¬Èí¼þÊÚÈ¨Ê§°Ü!", "ÏµÍ³´íÎó", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new MainEncrypter();
	}
}
