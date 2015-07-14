/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.monkeyrunner.recorder;

import com.android.monkeyrunner.MonkeyDevice;
import com.android.monkeyrunner.core.IMonkeyImage;
import com.android.monkeyrunner.core.IMonkeyDevice;
import com.android.monkeyrunner.recorder.actions.Action;
import com.android.monkeyrunner.recorder.actions.DragAction;
import com.android.monkeyrunner.recorder.actions.DragAction.Direction;
import com.android.monkeyrunner.recorder.actions.PressAction;
import com.android.monkeyrunner.recorder.actions.TouchAction;
import com.android.monkeyrunner.recorder.actions.TypeAction;
import com.android.monkeyrunner.recorder.actions.WaitAction;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * MainFrame for MonkeyRecorder.
 */
public class MonkeyRecorderFrame extends JFrame {
	private static final Logger LOG = Logger
			.getLogger(MonkeyRecorderFrame.class.getName());

	private final IMonkeyDevice device;

	public int startxx = 0;
	public int startyy = 0;
	public int endxx = 0;
	public int endyy = 0;
	public String stime,etime;
	
	GeneralPath gPath= new GeneralPath(); //GeneralPath对象实例
	Point aPoint; 
	
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel display = null;
	private JScrollPane historyPanel = null;
	private JPanel actionPanel = null;
	//private JButton waitButton = null;
	private JButton menuButton = null;
	private JButton homeButton = null;
	private JButton backButton = null;
	//private JButton pressButton = null;
	private JButton typeButton = null;
	//private JButton flingButton = null;
	private JButton westButton = null;
	private JButton eastButton = null;
	private JButton northButton = null;
	private JButton southButton = null;
	private JButton barButton = null;
	private JButton exportActionButton = null;
	private JButton refreshButton = null;

	private BufferedImage currentImage; // @jve:decl-index=0:
	private BufferedImage scaledImage = new BufferedImage(320, 480,
			BufferedImage.TYPE_INT_ARGB); // @jve:decl-index=0:

	private JList historyList;
	private ActionListModel actionListModel;

	private final Timer refreshTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			refreshDisplay(); // @jve:decl-index=0:
		}
	});

	/**
	 * This is the default constructor
	 */
	public MonkeyRecorderFrame(IMonkeyDevice device) {
		this.device = device;
		initialize();
	}

	private void initialize() {
		this.setSize(800, 1200);
		this.setContentPane(getJContentPane());
		this.setTitle("MonkeyRecorder");

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				refreshDisplay();
			}
		});
		refreshTimer.start();
	}
		
	private void refreshDisplay() {
		IMonkeyImage snapshot = device.takeSnapshot();
		currentImage = snapshot.createBufferedImage();

		Graphics2D g = scaledImage.createGraphics();
		g.drawImage(currentImage, 0, 0, scaledImage.getWidth(),
				scaledImage.getHeight(), null);
		g.draw(gPath); //绘制路径
		g.dispose();

		display.setIcon(new ImageIcon(scaledImage));

		pack();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			display = new JLabel();
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(display, BorderLayout.CENTER);
			jContentPane.add(getHistoryPanel(), BorderLayout.EAST);
			jContentPane.add(getActionPanel(), BorderLayout.NORTH);

			display.setPreferredSize(new Dimension(320, 480));

			display.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					touch(event);
				}
				public void mousePressed(MouseEvent event) {
					stime = new java.sql.Timestamp(System.currentTimeMillis()).toString();
					double scalex = ((double) currentImage.getWidth())
							/ ((double) scaledImage.getWidth());
					double scaley = ((double) currentImage.getHeight())
							/ ((double) scaledImage.getHeight());
					aPoint = event.getPoint(); //得到当前鼠标点
					startxx = aPoint.x;
					startyy = aPoint.y;
					startxx = (int) (startxx * scalex);
					startyy = (int) (startyy * scaley);
					System.out.println("startxx = " + startxx);
					System.out.println("startyy = " + startyy);
					gPath.moveTo(aPoint.x,aPoint.y); //设置路径点
				}
				public void mouseReleased(MouseEvent event) {
				etime = new java.sql.Timestamp(System.currentTimeMillis()).toString();
				System.out.println("getInitialDelay = " + returntime(stime,etime));
				
					double scalex = ((double) currentImage.getWidth())
							/ ((double) scaledImage.getWidth());
					double scaley = ((double) currentImage.getHeight())
							/ ((double) scaledImage.getHeight());
					aPoint = event.getPoint(); //得到当前鼠标点
					endxx = aPoint.x;
					endyy = aPoint.y;
					endxx = (int) (endxx * scalex);
					endyy = (int) (endyy * scaley);
					System.out.println("endxx = " + endxx);
					System.out.println("endyy = " + endyy);
				
					if (startxx == endxx && startyy == endyy)
						{
							if(returntime(stime,etime) < 300)
								System.out.println("touch event");
							else{
								System.out.println("longpress");
								addAction(newFlingAction(Direction.MOUSE, 1, 1000));
							}
						}
					else{
						addAction(newFlingAction(Direction.MOUSE, 10, 1000));
						}
					gPath.moveTo(aPoint.x,aPoint.y); //设置路径点
				}
			});
			
			display.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent event) {
					double scalex = ((double) currentImage.getWidth())
							/ ((double) scaledImage.getWidth());
					double scaley = ((double) currentImage.getHeight())
							/ ((double) scaledImage.getHeight());
					aPoint.x = (int) (aPoint.x * scalex);
					aPoint.y = (int) (aPoint.y * scaley);
					aPoint = event.getPoint(); //得到当前鼠标点
					gPath.lineTo(aPoint.x, aPoint.y); //设置路径
					gPath = new GeneralPath(); //重新实例化GeneralPath对象
					gPath.moveTo(aPoint.x, aPoint.y);
					refreshDisplay(); //重绘组件
				}
			});
			
		}
		return jContentPane;
	}

	/**
	 * This method initializes historyPanel
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getHistoryPanel() {
		if (historyPanel == null) {
			historyPanel = new JScrollPane();
			historyPanel.getViewport().setView(getHistoryList());
		}
		return historyPanel;
	}

	private JList getHistoryList() {
		if (historyList == null) {
			actionListModel = new ActionListModel();
			historyList = new JList(actionListModel);
		}
		return historyList;
	}

	/**
	 * This method initializes actionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getActionPanel() {
		if (actionPanel == null) {
			actionPanel = new JPanel();
			actionPanel.setLayout(new BoxLayout(getActionPanel(),
					BoxLayout.X_AXIS));
			//actionPanel.add(getWaitButton(), null);
			actionPanel.add(getMenuButton(), null);
			actionPanel.add(getHomeButton(), null);
			actionPanel.add(getBackButton(), null);
			//actionPanel.add(getPressButton(), null);
			//actionPanel.add(getFlingButton(), null);
			actionPanel.add(getWestButton(), null);
			actionPanel.add(getEastButton(), null);
			actionPanel.add(getNorthButton(), null);
			actionPanel.add(getSouthButton(), null);
			actionPanel.add(getBarButton(), null);
			actionPanel.add(getTypeButton(), null);
			actionPanel.add(getExportActionButton(), null);
			actionPanel.add(getRefreshButton(), null);
		}
		return actionPanel;
	}

	/**
	 * This method initializes waitButton
	 * 
	 * @return javax.swing.JButton
	 *
	 * removed by jude 20140109
	 */
	/**
	private JButton getWaitButton() {
		if (waitButton == null) {
			waitButton = new JButton();
			waitButton.setText("Wait");
			waitButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String howLongStr = JOptionPane
							.showInputDialog("How many seconds to wait?");
					if (howLongStr != null) {
						float howLong = Float.parseFloat(howLongStr);
						addAction(new WaitAction(howLong));
					}
				}
			});
		}
		return waitButton;
	}
	*/
	
	/**
	 * This method initializes pressButton
	 * 
	 * @return javax.swing.JButton
	 * 
	 * removed by jude 20140109
	 */
	/**
	private JButton getPressButton() {
		if (pressButton == null) {
			pressButton = new JButton();
			pressButton.setText("Press a Button");
			pressButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JPanel panel = new JPanel();
					JLabel text = new JLabel("What button to press?");
					JComboBox keys = new JComboBox(PressAction.KEYS);
					keys.setEditable(true);
					JComboBox direction = new JComboBox(
							PressAction.DOWNUP_FLAG_MAP.values().toArray());
					panel.add(text);
					panel.add(keys);
					panel.add(direction);

					int result = JOptionPane.showConfirmDialog(null, panel,
							"Input", JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION) {
						// Look up the "flag" value for the press choice
						Map<String, String> lookupMap = PressAction.DOWNUP_FLAG_MAP
								.inverse();
						String flag = lookupMap.get(direction.getSelectedItem());
						addAction(new PressAction((String) keys
								.getSelectedItem(), flag));
						addAction(new WaitAction(1));
					}
				}
			});
		}
		return pressButton;
	}*/

	/**
	 * This method initializes pressButton
	 * 
	 * @return javax.swing.JButton
	 */	
	private JButton getMenuButton() {
		if (menuButton == null) {
			menuButton = new JButton();
			menuButton.setText("MENU");
			menuButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				addAction(new PressAction("MENU"));
				addAction(new WaitAction(1));
				}
			});
		}
		return menuButton;
	}
	
	/**
	 * This method initializes pressButton
	 * 
	 * @return javax.swing.JButton
	 */	
	private JButton getHomeButton() {
		if (homeButton == null) {
			homeButton = new JButton();
			homeButton.setText("HOME");
			homeButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				addAction(new PressAction("HOME"));
				addAction(new WaitAction(1));
				}
			});
		}
		return homeButton;
	}

	/**
	 * This method initializes pressButton
	 * 
	 * @return javax.swing.JButton
	 */	
	private JButton getBackButton() {
		if (backButton == null) {
			backButton = new JButton();
			backButton.setText("BACK");
			backButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				addAction(new PressAction("BACK"));
				addAction(new WaitAction(1));
				}
			});
		}
		return backButton;
	}
	
	/**
	 * This method initializes typeButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getTypeButton() {
		if (typeButton == null) {
			typeButton = new JButton();
			typeButton.setText("Type");
			typeButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String whatToType = JOptionPane
							.showInputDialog("What to type?");
					if (whatToType != null) {
						addAction(new TypeAction(whatToType));
						addAction(new WaitAction(1));
					}
				}
			});
		}
		return typeButton;
	}

	/**
	 * This method initializes flingButton
	 * 
	 * @return javax.swing.JButton
	 * 
	 * removed by jude 20140110
	 */
	/**
	private JButton getFlingButton() {
		if (flingButton == null) {
			flingButton = new JButton();
			flingButton.setText("Fling");
			flingButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JPanel panel = new JPanel();
					panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
					panel.add(new JLabel("Which Direction to fling?"));
					JComboBox directionChooser = new JComboBox(
							DragAction.Direction.getNames());
					panel.add(directionChooser);
					panel.add(new JLabel("How long to drag (in ms)?"));
					JTextField ms = new JTextField();
					ms.setText("1000");
					panel.add(ms);
					panel.add(new JLabel("How many steps to do it in?"));
					JTextField steps = new JTextField();
					steps.setText("10");
					panel.add(steps);

					int result = JOptionPane.showConfirmDialog(null, panel,
							"Input", JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION) {
						DragAction.Direction dir = DragAction.Direction
								.valueOf((String) directionChooser
										.getSelectedItem());
						long millis = Long.parseLong(ms.getText());
						int numSteps = Integer.parseInt(steps.getText());

						addAction(newFlingAction(dir, numSteps, millis));
						addAction(new WaitAction(1));
					}
				}
			});
		}
		return flingButton;
	}
	*/
	
	/**
	 * This method initializes westButton
	 * 
	 * @return javax.swing.JButton
	 * 
	 * add by jude 20140109
	 */
	private JButton getWestButton() {
		if (westButton == null) {
			westButton = new JButton();
			westButton.setText("←");
			westButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
						addAction(newFlingAction(Direction.WEST, 10, 1000));
						addAction(new WaitAction(1));
				}
			});
		}
		return westButton;
	}
	
	/**
	 * This method initializes westButton
	 * 
	 * @return javax.swing.JButton
	 * 
	 * add by jude 20140109
	 */
	private JButton getEastButton() {
		if (eastButton == null) {
			eastButton = new JButton();
			eastButton.setText("→");
			eastButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
						addAction(newFlingAction(Direction.EAST, 10, 1000));
						addAction(new WaitAction(1));
				}
			});
		}
		return eastButton;
	}
	
	/**
	 * This method initializes westButton
	 * 
	 * @return javax.swing.JButton
	 * 
	 * add by jude 20140109
	 */
	private JButton getSouthButton() {
		if (southButton == null) {
			southButton = new JButton();
			southButton.setText("↓");
			southButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
						addAction(newFlingAction(Direction.SOUTH, 10, 1000));
						addAction(new WaitAction(1));
				}
			});
		}
		return southButton;
	}
	
	/**
	 * This method initializes westButton
	 * 
	 * @return javax.swing.JButton
	 * 
	 * add by jude 20140109
	 */
	private JButton getNorthButton() {
		if (northButton == null) {
			northButton = new JButton();
			northButton.setText("↑");
			northButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
						addAction(newFlingAction(Direction.NORTH, 10, 1000));
						addAction(new WaitAction(1));
				}
			});
		}
		return northButton;
	}
	
	//TOP BAR
	private JButton getBarButton() {
		if (barButton == null) {
			barButton = new JButton();
			barButton.setText("BAR");
			barButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
						addAction(newFlingAction(Direction.BAR, 10, 1000));
						addAction(new WaitAction(1));
				}
			});
		}
		return barButton;
	}
	//
	
	/**private void drag(MouseEvent er) { //鼠标运动事件处理
		int w = Integer.parseInt(device.getProperty("display.width"));
		int h = Integer.parseInt(device.getProperty("display.height"));
		
		int a = e.getX() - x;
        int b = e.getY() - y;
		
        if (a < 0) {
             a = 0;
         }
        if (b < 0) {
             b = 0;
         }
        
        if(e.getX()-xa>100){
            if(e.getY()-ya>100){
            System.out.println("方向---〉右下");
            addAction(newFlingAction(Direction.EAST, 10, 1000));
            }
            else if(e.getY()-ya<-100){
            System.out.println("方向---〉右上");
            addAction(newFlingAction(Direction.EAST, 10, 1000));
            }
            else{
            System.out.println("方向---〉右"); 
            addAction(newFlingAction(Direction.EAST, 10, 1000));
            }
           }
           else if(e.getX()-xa<0 && e.getX()-xa>-100){
            if(e.getY()-ya>100){
            System.out.println("方向---〉左下");
            addAction(newFlingAction(Direction.WEST, 10, 1000));
            }
            else if(e.getY()-ya<-100){
            System.out.println("方向---〉左上");
            addAction(newFlingAction(Direction.WEST, 10, 1000));
            }
            else{
            System.out.println("方向---〉左"); 
            addAction(newFlingAction(Direction.WEST, 10, 1000));
            }
           }
           else{
            if(e.getY()-ya>0 && e.getY()-ya<100){
            	System.out.println("方向---〉下");
            	addAction(newFlingAction(Direction.SOUTH, 10, 1000));
                }
                else if(e.getY()-ya<0 && e.getY()-ya>-100){
                System.out.println("方向---〉上");
                addAction(newFlingAction(Direction.NORTH, 10, 1000));
                }
           }
           xa = e.getX();   
           ya = e.getY();
		   if(e.getID() == MouseEvent.MOUSE_DRAGGED) {
		   addAction(newFlingAction(Direction.EAST, 10, 1000));
		}*/
	
	public DragAction newFlingAction(Direction dir, int numSteps, long millis) {
		int width = Integer.parseInt(device.getProperty("display.width"));
		int height = Integer.parseInt(device.getProperty("display.height"));

		// Adjust the w/h to a pct of the total size, so we don't hit things on
		// the "outside"
		width = (int) (width * 0.8f);
		height = (int) (height * 0.8f);
		int minW = (int) (width * 0.2f);
		int minH = (int) (height * 0.2f);

		int midWidth = width / 2;
		int midHeight = height / 2;

		
		int startx = minW;
		int starty = minH;
		int endx = minW;
		int endy = minH;
	
		switch (dir) {
		case MOUSE:
			startx = startxx;
			starty = startyy;
			endx = endxx;
			endy = endyy;
			break;
		case NORTH:
			startx = endx = midWidth;
			starty = height;
			break;
		case SOUTH:
			startx = endx = midWidth;
			endy = height;
			break;
		case EAST:
			starty = endy = midHeight;
			endx = width;
			break;
		case WEST:
			starty = endy = midHeight;
			startx = width;
			break;
		case BAR:
			startx = endx = midWidth;
			starty = height/height;
			endy = height;
			break;
		}
		addAction(new WaitAction(1));
		return new DragAction(dir, startx, starty, endx, endy, numSteps, millis);
	}
	
	/**
	 * This method initializes exportActionButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getExportActionButton() {
		if (exportActionButton == null) {
			exportActionButton = new JButton();
			exportActionButton.setText("Export");
			exportActionButton
					.addActionListener(new java.awt.event.ActionListener() {
						@Override
						public void actionPerformed(
								java.awt.event.ActionEvent ev) {
							JFileChooser fc = new JFileChooser();
							if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
								try {
									actionListModel.export(fc.getSelectedFile());
								} catch (FileNotFoundException e) {
									LOG.log(Level.SEVERE,
											"Unable to save file", e);
								}
							}
						}
					});
		}
		return exportActionButton;
	}

	/**
	 * This method initializes refreshButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton();
			refreshButton.setText("Refresh");
			refreshButton
					.addActionListener(new java.awt.event.ActionListener() {
						@Override
						public void actionPerformed(java.awt.event.ActionEvent e) {
							refreshDisplay();
						}
					});
		}
		return refreshButton;
	}

	private void touch(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();

		// Since we scaled the image down, our x/y are scaled as well.
		double scalex = ((double) currentImage.getWidth())
				/ ((double) scaledImage.getWidth());
		double scaley = ((double) currentImage.getHeight())
				/ ((double) scaledImage.getHeight());

		x = (int) (x * scalex);
		y = (int) (y * scaley);

		switch (event.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			addAction(new TouchAction(x, y, MonkeyDevice.DOWN_AND_UP));
			addAction(new WaitAction(1));
			break;
		case MouseEvent.MOUSE_PRESSED:
			addAction(new TouchAction(x, y, MonkeyDevice.DOWN));
			addAction(new WaitAction(1));
			break;
		case MouseEvent.MOUSE_RELEASED:
			addAction(new TouchAction(x, y, MonkeyDevice.UP));
			addAction(new WaitAction(1));
			break;
		}
	}
	
	public void addAction(Action a) {
		actionListModel.add(a);
		try {
			a.execute(device);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Unable to execute action!", e);
		}
	}
	public long returntime(String a, String b) {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long between = 0;
        try {
            java.util.Date begin = dfs.parse(a);
            java.util.Date end = dfs.parse(b);
            between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long ms = (between - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                - min * 60 * 1000 - s * 1000);
       // System.out.println(day + "天" + hour + "小时" + min + "分" + s + "秒" + ms+ "毫秒");
        return ms;
	}
}
