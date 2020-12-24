using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using FiscalPrinter.Properties;
using EltradeFPAx;
using System.Threading;
using System.IO;

namespace FiscalPrinter
{
    public partial class MainForm : Form
    {
        private NotifyIcon TrayIcon;
        EltradeFprn fp = new EltradeFprn();

        string cashier = "cashier";
        string tax = "";
        string cheque = "";
        string cash = "";
        string ccard = "";
        string spoint = "";
        string voucher = "";
        string eft = "";
        int PLUSTART = 0;
        int PLUPOS = 0;
        string PLUES = "";
        int beginget = 0;

        List<string> filed = new List<string>();
        List<string> extralines = new List<string>();

        BackgroundWorker backgroundWorker = new BackgroundWorker
        {
            WorkerReportsProgress = true,
            WorkerSupportsCancellation = true
        };
        private int start;
        private int flag;
        private string name;
        private string qty;
        private double prize;
        private string caash;
        private int taxg;
        private string invoice;

        public MainForm()
        {
            try
            {
                InitializeComponent();
                showtrayicon("Listener", "Started");
                folderpath_txt.Text = Settings.Default.path;
                filename_txt.Text = Settings.Default.filename;
                comport_txt.Text = Settings.Default.outpath.ToString();
                interval_txt.Text = Settings.Default.interval.ToString();
                toolStripStatusLabel1.Text = "Listening";

                backgroundWorker.DoWork += BackgroundWorkerOnDoWork;
                backgroundWorker.RunWorkerCompleted += backgroundWorker_RunWorkerCompleted;

                backgroundWorker.CancelAsync();
                startlistener();
            }catch
            {
                MessageBox.Show("An error occured whilst initialising the program");
            }
        }

        void showtrayicon(string message, string header)
        {
            try
            {
                TrayIcon = new NotifyIcon();
                ContextMenu m_menu;
                TrayIcon.Icon = this.Icon;
                TrayIcon.Visible = true;
                TrayIcon.ShowBalloonTip(1500, header, message, ToolTipIcon.Info);

                m_menu = new ContextMenu();
                m_menu.MenuItems.Add(0, new MenuItem("Settings", new System.EventHandler(SettingsMenuItem_Click)));
                m_menu.MenuItems.Add(1, new MenuItem("start listener", new System.EventHandler(startMenuItem_Click)));
                m_menu.MenuItems.Add(2, new MenuItem("stop listener", new System.EventHandler(stopMenuItem_Click)));
                m_menu.MenuItems.Add(3, new MenuItem("Exit", new System.EventHandler(exitMenuItem_Click)));

                TrayIcon.ContextMenu = m_menu;
            }catch
            {
                MessageBox.Show("An error occured whilst creating tray notification");
            }
        }

        private void SettingsMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                this.Show();
                this.WindowState = FormWindowState.Normal;
                this.ShowIcon = true;
                this.ShowInTaskbar= true;
            }catch
            {
                MessageBox.Show("An error occured whilst opening the settings page");
            }

        }

        private void exitMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                if (MessageBox.Show("Are you sure you want to exit?",
                        "Close Application?", MessageBoxButtons.YesNo, MessageBoxIcon.Question,
                        MessageBoxDefaultButton.Button2) == DialogResult.Yes)
                {
                    if (backgroundWorker.IsBusy)
                    {
                        backgroundWorker.CancelAsync();
                        toolStripStatusLabel1.Text = "Stopped";
                        pictureBox1.Visible = false;
                        
                    }
                    Settings.Default.active = false;
                    Settings.Default.Save();
                    Application.Exit();
                }
            }
            catch
            {
                MessageBox.Show("The program did not exit correctly");
            }
        }

        private void startMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                if (backgroundWorker.IsBusy)
                {
                    MessageBox.Show("Listener already Running");
                }
                else
                {
                    Settings.Default.active = true;
                    Settings.Default.Save();
                    pictureBox1.Visible = true;
                    toolStripStatusLabel1.Text = "Running";
                    backgroundWorker.RunWorkerAsync();
                }
            }catch
            {
                MessageBox.Show("An error occured when trying to start the listener");
            }
        }

        private void stopMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                if (backgroundWorker.IsBusy)
                {
                    backgroundWorker.CancelAsync();
                    toolStripStatusLabel1.Text = "Stopped";
                    pictureBox1.Visible = false;
                    Settings.Default.active = false;
                    Settings.Default.Save();
                }
                else
                {
                    MessageBox.Show("Listener not running");
                }
            }
            catch
            {
                MessageBox.Show("An error occured whilst stopping the listener");
            }


        }

        private void OnApplicationExit(object sender, EventArgs e)
        {
            try
            {
                TrayIcon.Visible = false;
                Settings.Default.active = false;
                Settings.Default.Save();
            }catch
            {
                MessageBox.Show("Application did not exit properly");
            }
        }

        private void folderpath_txt_Click(object sender, EventArgs e)
        {
            try
            {
                FolderBrowserDialog fd = new FolderBrowserDialog();
                DialogResult result = fd.ShowDialog();
                if (result == DialogResult.OK)
                {
                    folderpath_txt.Text = fd.SelectedPath;
                    Environment.SpecialFolder root = fd.RootFolder;
                }
            }catch
            {
                MessageBox.Show("Could not browse for folder path");
            }
        }

        private void btn_save_Click(object sender, EventArgs e)
        {
            try
            {
                Settings.Default.path = folderpath_txt.Text;
                Settings.Default.filename = filename_txt.Text;
                Settings.Default.outpath = comport_txt.Text;
                Settings.Default.interval = Convert.ToInt32(interval_txt.Text);
                Settings.Default.Save();
                MessageBox.Show("Settings Updated");
            }
            catch
            {
                MessageBox.Show("An error occcured whilst saving your settings");
            }
        }

        private void btn_about_Click(object sender, EventArgs e)
        {
            try
            {
                MessageBox.Show("Aura Group");
            }catch
            {
                MessageBox.Show("An error occured");
            }

        }

        private void btn_start_Click(object sender, EventArgs e)
        {
            try
            {
                if (backgroundWorker.IsBusy)
                {
                    MessageBox.Show("Listener already Running");
                }
                else
                {
                    toolStripStatusLabel1.Text = "Running";
                    pictureBox1.Visible = true;
                    Settings.Default.active = true;
                    Settings.Default.Save();
                    backgroundWorker.RunWorkerAsync();
                }
            }
            catch
            {
                MessageBox.Show("An error occured when trying to start the listener");
            }
        }

        private void startlistener()
        {
            try
            {
                if (Settings.Default.active == true)
                {

                }
                else
                {
                    Settings.Default.active = true;
                    Settings.Default.Save();
                    backgroundWorker.RunWorkerAsync();
                    pictureBox1.Visible = true;
                    toolStripStatusLabel1.Text = "Running";
                }
            }catch
            {
                MessageBox.Show("An error occured whilst starting the listener");
            }
            
        }
        private void getdataWoodtechnology()
        {
            try
            {
                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);

                filed.Add("##DLRWF##");
                filed.Add("#*1#1#IVAN#0.00#14.5#0.00#0.00#1#1#1#0#");

                foreach (string line in lines)
                {
                    string test = line;
                    if (test == String.Empty)
                    {
                        continue;
                    }

                    else if (test.Contains("TEL NO."))
                    {
                        int k = test.Length;
                        k = k - 96;



                        invoice = test.Substring(96, k).Trim();
                        filed.Add("#!1234567890#");

                    }

                    //  MessageBox.Show(test);

                    else if (test.Contains("Description"))
                    {
                        //MessageBox.Show("WW");
                        start = 1;
                        flag = 0;
                    }
                    else if (start == 1 && flag == 0)
                    {
                        string code = test.Substring(0,15);
                        name = (test.Substring(16, 55).Trim());
                        name = name.Trim();
                        // MessageBox.Show(name);
                        qty = test.Substring(60, 10).Trim();
                        //MessageBox.Show(qty);
                        string price = test.Substring(71, 12).Trim();
                        // MessageBox.Show(price);
                        prize = Convert.ToDouble(price);

                        prize = prize * 1.15;
                        string tax = test.Substring(80, 10);
                        if (tax.Contains("15.00%"))
                        {
                            taxg = 2;
                        }
                        if (name.Length > 5 && prize > 1 && qty.Length > 0)
                        {
                            //#^123#Soft Drink#123.45#2.000#2#
                            filed.Add("#^" + code + "#" + name + "#" + price + "#" + qty + "#" + taxg + "#");
                        }

                        //  MessageBox.Show(filed.ToString());


                    }
                    else if (start == 1 && test.Replace(" ", string.Empty) == "")
                    {
                        start = 0;
                        flag = 1;
                    }




                }


                clean();

            }
            catch
            {

                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"C:/receipts/receipt.txt");
                foreach (string lineaa in lines)
                {
                    string test = lineaa;


                    if (test.Contains("Received in good order"))
                    {
                        flag = 1;
                    }
                    else if (flag == 1 && test.Contains("$"))
                    {
                        caash = test.Split('$')[1].Trim();
                        caash = caash.Replace("$", string.Empty);
                        //MessageBox.Show(caash);

                    }

                }
                filed.Add("#$" + "1" + "#" + caash + "#");
                System.IO.File.WriteAllLines(@"C:/receipts/unproccesed/" + invoice + ".prn", filed);
                //  File.Move("c:/Receipts/receipt.txt", "c:/Receipts/backup"+"/"+ DateTime.Now.ToString()+"");
                File.Delete(@"C:/receipts.receipt.txt");
                File.Delete(@"" + path + "\\" + filename);
                // fp.Close();
                //  MessageBox.Show("done");


            }


        }
        private void getJnP()
        {
            try
            {
                filed.Clear();
                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);

                filed.Add("##DLRWF##");
                filed.Add("#*1#1#IVAN#0.00#14.50#0.00#0.00#1#1#1#0#");

                foreach (string line in lines)
                {
                    string test = line;
                    if (test == String.Empty)
                    {
                        continue;
                    }

                    if (test.Length < 40)
                    {
                        //MessageBox.Show("empty");
                        continue;
                    }

                    else if (test.Contains("Invoice No"))
                    {
                        int k = test.Length;
                        k = k - 79;



                        invoice = test.Substring(79, k).Trim();
                        filed.Add("#!1234567890#");
                        //MessageBox.Show(invoice);

                    }

                    //  MessageBox.Show(test);

                    else if (test.Contains("Description"))
                    {
                        //MessageBox.Show("WW");
                        start = 1;
                        flag = 0;
                    }
                    else if (start == 1 && flag == 0)
                    {
                        int len = test.Length;
                        len = len - 108;
                        qty = test.Substring(0, 6).Trim();
                        qty = qty.Replace(",", string.Empty);
                        //MessageBox.Show(qty);

                        string code = test.Substring(7, 5).Trim();
                        //MessageBox.Show(code);
                        name = (test.Substring(13, 27).Trim());
                        name = name.Trim();
                        //MessageBox.Show(name);
                        

                        string price = test.Substring(41, 16).Trim();
                        price = price.Replace("$", string.Empty);
                        price = price.Replace(",", string.Empty);
                        //MessageBox.Show(price);
                        
                        prize = Convert.ToDouble(price);

                        prize = prize * 1.145;
                        //string tax = test.Substring(108, len);

                        taxg = 2;
                        
                        
                        if (name.Length > 1 && qty.Length > 0)
                        {
                            //#^123#Soft Drink#123.45#2.000#2#
                            filed.Add("#^" + code + "#" + name + "#" + prize + "#" + qty + "#" + taxg + "#");
                            //MessageBox.Show("Good");
                        }

                          //MessageBox.Show(filed.ToString());


                    }
                    else if (start == 1 && test.Replace(" ", string.Empty) == "")
                    {
                        start = 0;
                        flag = 1;
                    }




                }


                clean();

            }
            catch
            {

                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"C:/receipts/receipt.txt");
                foreach (string lineaa in lines)
                {
                    string test = lineaa;


                    if (test.Contains("V.A.T"))
                    {
                        flag = 1;
                    }
                    else if (flag == 1 && test.Contains("Total"))
                    {
                        int aa = test.Length;
                        aa = aa - 85;


                        caash = test.Substring(85, aa).Trim();//Split('$')[1].Trim();
                        caash = caash.Replace("$", string.Empty);
                        caash = caash.Replace(",", string.Empty);
                        caash = caash.Replace(" ", string.Empty);
                        //MessageBox.Show(caash);
                        //MessageBox.Show("Stop");

                    }

                }
                filed.Add("#$" + "1" + "#" + caash + "#");
                System.IO.File.WriteAllLines(@"C:/receipts/unprocessed/" + invoice + ".prn", filed);
                //  File.Move("c:/Receipts/receipt.txt", "c:/Receipts/backup"+"/"+ DateTime.Now.ToString()+"");
                File.Delete(@"C:/receipts.receipt.txt");
                File.Delete(@"" + path + "\\" + filename);
                // fp.Close();
                //  MessageBox.Show("done");
                filed.Clear();


            }


        }

        private void getdatarapidgain()
        {
            try
            {
                filed.Clear();
                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);

                filed.Add("##DLRWF##");
                filed.Add("#*1#1#IVAN#0.00#15#0.00#0.00#1#1#1#0#");

                foreach (string line in lines)
                {
                    string test = line;
                    if (test == String.Empty)
                    {
                        continue;
                    }

                   else if (test.Contains("Invoice No.") || test.Contains("Cr. Note No."))
                    {
                        int k =  test.Length;
                        k = k - 90;
                        
                            
                            
                        invoice = test.Substring(90, k).Trim();
                        invoice = invoice.Replace(":", string.Empty);
                        filed.Add("#!1234567890#");
                        MessageBox.Show(invoice);

                    }

                    //  MessageBox.Show(test);

                    else if (test.Contains("Number      Weight (MT)") || test.Contains("Description of Goods"))
                    {
                        MessageBox.Show("WW");
                        start = 1;
                        flag = 0;
                    }
                    else if (start == 1 && flag == 0)
                    {
                        int len = test.Length;
                        len = len - 137;
                        string code = test.Substring(0,30);
                        MessageBox.Show(code);
                        name = (test.Substring(30, 22).Trim());
                        name = name.Trim();
                       // if (name == string.Empty)
                       // {
                          //  name = "Goods carried";
                        //}
                       MessageBox.Show(name);
                        //qty = test.Substring(63, 10).Trim();
                        //MessageBox.Show(qty);
                        string price = test.Substring(137, len).Trim();
                        
                       MessageBox.Show(price);
                        prize = Convert.ToDouble(price);

                        prize = prize * 1.15;
                        taxg = 2;
                        //string tax = test.Substring(108, len);
                        /*if (tax.Contains("Tax"))
                        {
                            taxg = 2;
                        }
                        else
                        {
                            taxg = 1;
                        }*/
                        if (code.Length > 5 && name.Length > 3 && prize > 0  )
                        {
                            //#^123#Soft Drink#123.45#2.000#2#
                            filed.Add("#^" + code + "#" + name + "#" + prize + "#" + 1 + "#" + taxg + "#");
                        }

                      //  MessageBox.Show(filed.ToString());


                    }
                    else if (start == 1 && test.Replace(" ", string.Empty) == "")
                    {
                        start = 0;
                        flag = 1;
                    }




                }


                clean();

            }
            catch
            {

                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"C:/receipts/receipt.txt");
                int set = 0;
                foreach (string lineaa in lines)
                {
                    string test = lineaa;


                    if (test.Contains("Sub Total "))
                    {
                        flag = 1;
                    }
                    else if (flag == 1  && test.Contains("Total Amount "))
                    {
                        set = 1;
                        MessageBox.Show("mon");
                        int fi = test.Length;
                        fi = fi - 137;
                        caash = test.Substring(137, fi);// Split('$')[1].Trim();
                        caash = caash.Replace("$", string.Empty);
                        caash = caash.Replace("($)", string.Empty);
                        caash = caash.Replace(",", string.Empty);
                       // caash = caash.Replace(" ", string.Empty);
                        MessageBox.Show(caash);

                    }
                    set = 0;
                }
                filed.Add("#$" + "1" + "#" + caash + "#");
                
                System.IO.File.WriteAllLines(@"C:/receipts/unprocessed/"+ "\\" + invoice +".prn", filed);
                MessageBox.Show("yes");
                //  File.Move("c:/Receipts/receipt.txt", "c:/Receipts/backup"+"/"+ DateTime.Now.ToString()+"");
                File.Delete(@"C:/receipts.receipt.txt");
                File.Delete(@"" + path + "\\" + filename);
                // fp.Close();
                //  MessageBox.Show("done");
                filed.Clear();


            }


        }
       
        private void getdatatoparch()
        {
            try
            {
                filed.Clear();
                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);
                string dt = DateTime.Now.Year.ToString();
                filed.Add("##DLRWF##");
                filed.Add("#*1#1#IVAN#0.00#15#0.00#0.00#1#1#1#0#");

                foreach (string line in lines)
                {
                    string test = line;
                    if (test == String.Empty)
                    {
                        continue;
                    }
                    else if (test.Length < 47)
                    {
                        continue;
                    }

                    else if (test.Contains("Invoice No"))
                    {
                        //MessageBox.Show("invoice");
                        int k = test.Length;
                        k = k - 99;



                        invoice = test.Trim();//Substring(99, k).Trim();
                        invoice = invoice.Replace("Invoice No.:", string.Empty);
                        filed.Add("#!1234567890#");

                    }

                    //  MessageBox.Show(test);

                    else if (test.Contains("Description"))
                    {
                        //MessageBox.Show("WW");
                        start = 1;
                        flag = 0;
                    }
                    else if (start == 1 && flag == 0)
                    {
                        int len = test.Length;
                        len = len - 108;
                        //string code = test.Substring(0,15).Trim();
                        qty = test.Substring(0, 10).Trim();
                        qty = qty.Replace(",", string.Empty);
                        //MessageBox.Show(qty);
                        name = (test.Substring(14, 33).Trim());
                        name = name.Trim();
                        //MessageBox.Show(name);
                       // qty = test.Substring(53, 10).Trim();
                        //MessageBox.Show(qty);
                        string price = test.Substring(47, 17).Trim();
                        price = price.Replace("$", string.Empty);
                        price = price.Replace(",", string.Empty);


                        //MessageBox.Show(price);
                        prize = Convert.ToDouble(price);

                         taxg = 1;
                        
                        if (name.Length > 3 && prize > 0 && qty.Length > 0)
                        {
                            //#^123#Soft Drink#123.45#2.000#2#
                            filed.Add("#^" + "123" + "#" + name + "#" + price + "#" + qty + "#" + taxg + "#");
                        }

                        //  MessageBox.Show(filed.ToString());


                    }
                    else if (start == 1 && test.Replace(" ", string.Empty) == "")
                    {
                        start = 0;
                        flag = 1;
                    }




                }


        

            }
            catch
            {

                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);
                foreach (string lineaa in lines)
                {
                    string test = lineaa;

                    
                    if (test.Contains("Subtotal"))
                    {
                        //MessageBox.Show("sss");
                        flag = 1;
                    }
                    else if (flag == 1 && test.Contains("Total Incl"))
                    {
                        int ds = test.Length;
                        ds = ds - 100;
                        caash = test.Trim();//Substring(100,ds).Trim();
                        caash = caash.Replace("Total Incl", string.Empty);
                        caash = caash.Replace("$", string.Empty);
                        caash = caash.Replace(",", string.Empty);
                        //MessageBox.Show(caash);

                    }

                }
                filed.Add("#$" + "1" + "#" + caash + "#");
                System.IO.File.WriteAllLines(@"C:/receipts/unprocessed/" + invoice + ".prn", filed);
                //  File.Move("c:/Receipts/receipt.txt", "c:/Receipts/backup"+"/"+ DateTime.Now.ToString()+"");
               // File.Delete(@"C:/receipts.receipt.txt");
                File.Delete(@"" + path + "\\" + filename);
                // fp.Close();
                //  MessageBox.Show("done");
                filed.Clear();


            }


        }
        private void getdataesd()
        {
            try
            {
                filed.Clear();
                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);
                string dt = DateTime.Now.Year.ToString();
                filed.Add("##DLRWF##");
                filed.Add("#*1#1#IVAN#0.00#15#0.00#0.00#1#1#1#0#");

                foreach (string line in lines)
                {
                    string test = line;
                    if (test == String.Empty)
                    {
                        continue;
                    }

                    else if (test.Contains(dt))
                    {
                        int k = test.Length;
                        k = k - 96;



                        invoice = test.Substring(96, k).Trim();
                      //  MessageBox.Show(invoice);
                        filed.Add("#!1234567890#");

                    }

                    //  MessageBox.Show(test);

                    else if (test.Contains("Description"))
                    {
                       // MessageBox.Show("WW");
                        start = 1;
                        flag = 0;
                    }
                    else if (start == 1 && flag == 0)
                    {
                        int len = test.Length;
                        len = len - 108;
                        string code = test.Substring(0, 15).Trim();
                        name = (test.Substring(21, 30).Trim());
                        name = name.Trim();
                       //  MessageBox.Show(name);
                        qty = test.Substring(53,10).Trim(); //test.Substring(63, 10).Trim();
                       // MessageBox.Show(qty);
                        string price = test.Substring(73, 11).Trim();
                        price = price.Replace("$",string.Empty);
                        price = price.Replace(",", string.Empty);

                        // MessageBox.Show(price);
                        prize = Convert.ToDouble(price);

                        
                        taxg = 2;

                        if (name.Length > 3 && price.Length > 1 && qty.Length > 0)
                        {
                            //#^123#Soft Drink#123.45#2.000#2#
                            filed.Add("#^" + code + "#" + name + "#" + prize + "#" + qty + "#" + taxg + "#");
                        }

                        //  MessageBox.Show(filed.ToString());


                    }
                  




                }


                clean();

            }
            catch
            {

                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);
                foreach (string lineaa in lines)
                {
                    string test = lineaa;


                    if (test.Contains("Discount"))
                    {
                        //MessageBox.Show("flag");
                        flag = 1;
                    }
                    else if (flag == 1 && test.Contains("Total (Incl)"))
                    {
                        int aa = test.Length;
                        aa = aa - 100;


                        caash = test.Substring(100, aa).Trim();//Split('$')[1].Trim();
                        caash = caash.Replace("$", string.Empty);
                        caash = caash.Replace(",", string.Empty);
                       // MessageBox.Show(caash);

                    }

                }
                filed.Add("#$" + "1" + "#" + caash + "#");
                System.IO.File.WriteAllLines(@"C:/receipts/unprocessed" + "\\" + invoice + ".prn", filed);
                //  File.Move("c:/Receipts/receipt.txt", "c:/Receipts/backup"+"/"+ DateTime.Now.ToString()+"");
                File.Delete(@"C:/receipts.receipt.txt");
                File.Delete(@"" + path + "\\" + filename);
                // fp.Close();
                 // MessageBox.Show("done");
                filed.Clear();


            }


        }
        private void getdatayellow()
        {
            try
            {
                filed.Clear();
                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);
                string dt = DateTime.Now.Year.ToString();
                filed.Add("##DLRWF##");
                filed.Add("#*1#1#IVAN#0.00#15#0.00#0.00#1#1#1#0#");

                foreach (string line in lines)
                {
                    string test = line;
                    if (test == String.Empty)
                    {
                        continue;
                    }

                    else if (test.Contains(dt))
                    {
                        int k = test.Length;
                        k = k - 96;



                        invoice = test.Substring(96, k).Trim();
                        //  MessageBox.Show(invoice);
                        filed.Add("#!1234567890#");

                    }

                    //  MessageBox.Show(test);

                    else if (test.Contains("Description"))
                    {
                        // MessageBox.Show("WW");
                        start = 1;
                        flag = 0;
                    }
                    else if (start == 1 && flag == 0)
                    {
                        int len = test.Length;
                        len = len - 108;
                        string code = test.Substring(0, 10).Trim();
                        name = (test.Substring(11, 30).Trim());
                        name = name.Trim();
                        //  MessageBox.Show(name);
                        qty = test.Substring(59, 10).Trim(); //test.Substring(63, 10).Trim();
                                                             // MessageBox.Show(qty);
                        string price = test.Substring(73, 11).Trim();
                        price = price.Replace("$", string.Empty);
                        price = price.Replace(",", string.Empty);

                        // MessageBox.Show(price);
                        prize = Convert.ToDouble(price);
                        prize = prize * 1.15;


                        taxg = 2;

                        if (name.Length > 3 && price.Length > 1 && qty.Length > 0 && code.Length >0)
                        {
                            //#^123#Soft Drink#123.45#2.000#2#
                            filed.Add("#^" + code + "#" + name + "#" + prize + "#" + qty + "#" + taxg + "#");
                        }

                        //  MessageBox.Show(filed.ToString());


                    }





                }


                clean();

            }
            catch
            {

                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);
                foreach (string lineaa in lines)
                {
                    string test = lineaa;


                    if (test.Contains("Discount"))
                    {
                        //MessageBox.Show("flag");
                        flag = 1;
                    }
                    else if (flag == 1 && test.Contains("Total (Incl)"))
                    {
                        int aa = test.Length;
                        aa = aa - 100;


                        caash = test.Substring(100, aa).Trim();//Split('$')[1].Trim();
                        caash = caash.Replace("$", string.Empty);
                        caash = caash.Replace(",", string.Empty);
                        // MessageBox.Show(caash);

                    }

                }
                filed.Add("#$" + "1" + "#" + caash + "#");
                System.IO.File.WriteAllLines(@"C:/receipts/unprocessed" + "\\" + invoice + ".prn", filed);
                //  File.Move("c:/Receipts/receipt.txt", "c:/Receipts/backup"+"/"+ DateTime.Now.ToString()+"");
                File.Delete(@"C:/receipts.receipt.txt");
                File.Delete(@"" + path + "\\" + filename);
                // fp.Close();
                // MessageBox.Show("done");
                filed.Clear();


            }


        }
        private void getdatakhatri()
        {
            try
            {
                filed.Clear();
                string path = Settings.Default.path;

                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);
                string dt = DateTime.Now.Year.ToString();
                filed.Add("##DLRWF##");
                filed.Add("#*1#1#IVAN#0.00#14.50#0.00#0.00#1#1#1#0#");

                foreach (string line in lines)
                {
                    string test = line;
                    if (test == String.Empty)
                    {
                        continue;
                    }

                    else if (test.Contains("Document  No"))
                    {
                        int k = test.Length;
                        k = k - 96;



                        invoice = test.Substring(96, k).Trim();
                        //  MessageBox.Show(invoice);
                        filed.Add("#!1234567890#");

                    }

                    //  MessageBox.Show(test);

                    else if (test.Contains("Description"))
                    {
                        // MessageBox.Show("WW");
                        start = 1;
                        flag = 0;
                    }
                    else if (start == 1 && flag == 0)
                    {
                        int len = test.Length;
                        len = len - 115;
                        string code = test.Substring(0, 16).Trim();
                        name = (test.Substring(17, 40).Trim());
                        name = name.Trim();
                        //MessageBox.Show(name);
                        //qty = test.Substring(60, 20).Trim(); //test.Substring(63, 10).Trim();
                                                             // MessageBox.Show(qty);
                        string price = test.Substring(115, len).Trim();
                        price = price.Replace("$", string.Empty);
                        price = price.Replace(",", string.Empty);

                        //MessageBox.Show(price);
                        prize = Convert.ToDouble(price);
                        


                        taxg = 2;

                        if (name.Length > 3 && price.Length > 1 && code.Length > 0)
                        {
                            //#^123#Soft Drink#123.45#2.000#2#
                            filed.Add("#^" + code + "#" + name + "#" + prize + "#" + 1 + "#" + taxg + "#");
                        }

                        //  MessageBox.Show(filed.ToString());


                    }





                }


                clean();

            }
            catch
            {

                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);
                foreach (string lineaa in lines)
                {
                    string test = lineaa;


                    if (test.Contains("Amount Excl Tax"))
                    {
                        //MessageBox.Show("flag");
                        flag = 1;
                    }
                    else if (flag == 1 && test.Contains("Total"))
                    {
                        int aa = test.Length;
                        aa = aa - 105;


                        caash = test.Substring(105, aa).Trim();//Split('$')[1].Trim();
                        caash = caash.Replace("$", string.Empty);
                        caash = caash.Replace(",", string.Empty);
                        //MessageBox.Show(caash);

                    }

                }
                filed.Add("#$" + "1" + "#" + caash + "#");
                System.IO.File.WriteAllLines(@"C:/receipts/unprocessed" + "\\" + invoice + ".prn", filed);
                //  File.Move("c:/Receipts/receipt.txt", "c:/Receipts/backup"+"/"+ DateTime.Now.ToString()+"");
                File.Delete(@"C:/receipts.receipt.txt");
                File.Delete(@"" + path + "\\" + filename);
                // fp.Close();
                // MessageBox.Show("done");
                filed.Clear();


            }


        }
        private void getdatadollarmotors()
        {
            try
            {
                filed.Clear();
                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string dt = DateTime.Now.Year.ToString();
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);

                filed.Add("##DLRWF##");
                filed.Add("#*1#1#IVAN#0.00#15#0.00#0.00#1#1#1#0#");

                foreach (string line in lines)
                {
                    string test = line;
                    if (test == String.Empty)
                    {
                        continue;
                    }

                    else if (test.Contains(dt))
                    {
                        int k = test.Length;
                        k = k - 84;



                        invoice = test.Substring(84, k).Trim();
                        filed.Add("#!1234567890#");
                        //MessageBox.Show(invoice);

                    }



                    else if (test.Contains("Description "))
                    {
                        //MessageBox.Show("WW");
                        start = 1;
                        flag = 0;
                    }
                    else if (start == 1 && flag == 0)
                    {
                        int len = test.Length;
                        len = len - 78;
                       // string code = test.Substring(0, 20).Trim();
                        name = (test.Substring(0, 36).Trim());
                        name = name.Trim();
                        //MessageBox.Show(name);
                        qty = "1"; //test.Substring(63, 10).Trim();
                        //MessageBox.Show(qty);
                        string price = test.Substring(50, 16).Trim();
                        price = price.Replace(",", string.Empty);

                        //MessageBox.Show(price);
                        prize = Convert.ToDouble(price);

                        
                        string tax = test.Substring(78, len);
                        if (tax.Contains("T"))
                        {
                            prize = prize * 1.145;
                            taxg = 2;
                        }
                        else
                        {
                            prize = prize * 1;
                            taxg = 1;
                        }

                        

                        if (name.Length > 3 && prize > 1 && qty.Length > 0)
                        {
                            //#^123#Soft Drink#123.45#2.000#2#
                            filed.Add("#^" + "123" + "#" + name + "#" + prize + "#" + qty + "#" + taxg + "#");
                        }

                        //  MessageBox.Show(filed.ToString());


                    }
                    else if (start == 1 && test.Replace(" ", string.Empty) == "")
                    {
                        start = 0;
                        flag = 1;
                    }




                }


                clean();

            }
            catch
            {

                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"C:/receipts/receipt.txt");
                foreach (string lineaa in lines)
                {
                    string test = lineaa;


                    if (test.Contains("Subtotal "))
                    {
                        flag = 1;
                    }
                    else if (flag == 1 && test.Contains("Invoice Total  "))
                    {
                        int aa = test.Length;
                        aa = aa - 106;


                        caash = test.Trim();//Substring(106, aa)//Split('$')[1].Trim();
                        caash = caash.Replace("Invoice Total ", string.Empty);
                        caash = caash.Replace(" ", string.Empty);
                        caash = caash.Replace("US$", string.Empty);
                        caash = caash.Replace(",", string.Empty);
                        //MessageBox.Show(caash);

                    }

                }
                filed.Add("#$" + "1" + "#" + caash + "#");
                System.IO.File.WriteAllLines(@"C:/receipts/unprocessed/" + invoice + ".prn", filed);
                //  File.Move("c:/Receipts/receipt.txt", "c:/Receipts/backup"+"/"+ DateTime.Now.ToString()+"");
                File.Delete(@"C:/receipts.receipt.txt");
                File.Delete(@"" + path + "\\" + filename);
                // fp.Close();
                //  MessageBox.Show("done");
                filed.Clear();


            }


        }
        private void getdatasunburn()
        {
            try
            {
                filed.Clear();
                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"" + path + "\\" + filename);

                filed.Add("##DLRWF##");
                filed.Add("#*1#1#IVAN#0.00#15#0.00#0.00#1#1#1#0#");

                foreach (string line in lines)
                {
                    string test = line;
                    if (test == String.Empty)
                    {
                        continue;
                    }

                    else if (test.Contains("TEL NO"))
                    {
                        int k = test.Length;
                        k = k - 96;



                        invoice = test.Substring(96, k).Trim();
                        filed.Add("#!1234567890#");

                    }

                    //  MessageBox.Show(test);

                    else if (test.Contains("Description"))
                    {
                        //MessageBox.Show("WW");
                        start = 1;
                        flag = 0;
                    }
                    else if (start == 1 && flag == 0)
                    {
                        int len = test.Length;
                        len = len - 108;
                       // string code = test.Substring(0, 20).Trim();
                        name = (test.Substring(0, 30).Trim());
                        name = name.Trim();
                        // MessageBox.Show(name);
                        qty = test.Substring(56,10).Trim(); //test.Substring(63, 10).Trim();
                        //MessageBox.Show(qty);
                        string price = test.Substring(69, 10).Trim();

                        // MessageBox.Show(price);
                        prize = Convert.ToDouble(price);

                        prize = prize * 1.15;
                        string tax = test.Substring(108, len);

                        taxg = 2;

                        if (name.Length > 3 && prize > 1 && qty.Length > 0)
                        {
                            //#^123#Soft Drink#123.45#2.000#2#
                            filed.Add("#^" + "" + "#" + name + "#" + prize + "#" + qty + "#" + taxg + "#");
                        }

                        //  MessageBox.Show(filed.ToString());


                    }
                    else if (start == 1 && test.Replace(" ", string.Empty) == "")
                    {
                        start = 0;
                        flag = 1;
                    }




                }


                clean();

            }
            catch
            {

                string path = Settings.Default.path;
                string filename = Settings.Default.filename;
                string outpath = Settings.Default.outpath;
                string[] lines = System.IO.File.ReadAllLines(@"C:/receipts/receipt.txt");
                foreach (string lineaa in lines)
                {
                    string test = lineaa;


                    if (test.Contains("Received in good order "))
                    {
                        flag = 1;
                    }
                    else if (flag == 1 && test.Contains("Total"))
                    {
                        int aa = test.Length;
                        aa = aa - 106;


                        caash = test.Split('$')[1].Trim();
                        caash = caash.Replace("$", string.Empty);
                        //MessageBox.Show(caash);

                    }

                }
                filed.Add("#$" + "1" + "#" + caash + "#");
                System.IO.File.WriteAllLines(@"C:/receipts/unprocessed/" + invoice + ".prn", filed);
                //  File.Move("c:/Receipts/receipt.txt", "c:/Receipts/backup"+"/"+ DateTime.Now.ToString()+"");
                File.Delete(@"C:/receipts.receipt.txt");
                File.Delete(@"" + path + "\\" + filename);
                // fp.Close();
                //  MessageBox.Show("done");
                filed.Clear();


            }


        }
        private void clean()
        {
            try
            {
                //iList.Clear();
                extralines.Clear();
                PLUSTART = 0;
                PLUPOS = 0;
                PLUES = "";
                beginget = 0;
            }catch
            {
                MessageBox.Show("An error occured whilst clearing values");
            }
        }

        private void BackgroundWorkerOnDoWork(object sender, DoWorkEventArgs e)
        {
            BackgroundWorker worker = (BackgroundWorker)sender;
            try
            {
                while (!worker.CancellationPending)
                {
                    if (File.Exists("" + Settings.Default.path + "\\" + Settings.Default.filename))
                    {
                        getJnP();
                    }

                    Thread.Sleep(Settings.Default.interval);
                    worker.ReportProgress(0, "AN OBJECT TO PASS TO THE UI-THREAD");
                }
            }catch
            {
                MessageBox.Show("An error occured , could not start listening");
            }
        }

        private void onerror()
        {
            if (InvokeRequired)
                {
                    MethodInvoker method = new MethodInvoker(onerror);
                    Invoke(method);
                    return;
                }
           
            try
            {
                backgroundWorker.CancelAsync();
                this.Show();
                this.WindowState = FormWindowState.Normal;
                this.ShowIcon = true;
                this.ShowInTaskbar = true;
                this.toolStripStatusLabel1.Text = "Listener Stopped";
                this.pictureBox1.Visible = false;

                MessageBox.Show("Unable to connect to printer");
            }catch
            {
                MessageBox.Show("An error occured whilst handling another error");
            }
        }

        private void btn_stop_Click(object sender, EventArgs e)
        {
            try
            {
                if (backgroundWorker.IsBusy)
                {
                    backgroundWorker.CancelAsync();
                    toolStripStatusLabel1.Text = "Stopped";
                    pictureBox1.Visible = false;
                    Settings.Default.active = false;
                    Settings.Default.Save();
                }
                else
                {
                    MessageBox.Show("Listener not running");
                }
            }catch
            {
                MessageBox.Show("An error occured whilst stopping the listener");
            }  
        }

        private void btn_hide_Click(object sender, EventArgs e)
        {
            try
            {
                this.Hide();
            }catch
            {
                MessageBox.Show("An error ocuured whilst hiding your form");
            }
        }

        private void btn_exit_Click(object sender, EventArgs e)
        {
            try
            {
                if (MessageBox.Show("Are you sure you want to exit?",
                        "Close Application?", MessageBoxButtons.YesNo, MessageBoxIcon.Question,
                        MessageBoxDefaultButton.Button2) == DialogResult.Yes)
                {
                    if (backgroundWorker.IsBusy)
                    {
                        backgroundWorker.CancelAsync();
                        toolStripStatusLabel1.Text = "Stopped";
                        pictureBox1.Visible = false;

                    }
                    Settings.Default.active = false;
                    Settings.Default.Save();
                    Application.Exit();
                }
            }
            catch
            {
                MessageBox.Show("The program did not exit correctly");
            }
        }

        private void backgroundWorker_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            if (e.Cancelled == true)
            {
                MessageBox.Show( "Canceled!");
            }
            else if (e.Error != null)
            {
               MessageBox.Show( "Error: " + e.Error.Message);
            }
            else
            {
                this.WindowState = FormWindowState.Normal;
                this.ShowIcon = true;
                this.Show();
            }
        }

        private void zReportToolStripMenuItem_Click(object sender, EventArgs e)
        {
            string dt =  DateTime.Now.ToString();
            string zreport = "zreport";//DateTime.Now.ToString();

            filed.Add("##DLRWF#");
            filed.Add("#N1");
            //System.IO.File.WriteAllLines(@"C:/receipts/unproccesed/" + dd +"report.prn", filed);
            System.IO.File.WriteAllLines(@"C:/receipts/unprocessed/"+zreport+".prn", filed);
            MessageBox.Show("Z report Printed Successfully");
            //System.Diagnostics.Process.Start(@"C:/receipts/proccesed/" + zreport + ".html");
        }

        private void optionsToolStripMenuItem_Click(object sender, EventArgs e)
        {

        }
    }
}
