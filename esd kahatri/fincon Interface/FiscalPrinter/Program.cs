using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using FiscalPrinter.Properties;

namespace FiscalPrinter
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Settings.Default.active = false;
            Settings.Default.Save();
            Application.Run(new MainForm());
        }
    }
}
