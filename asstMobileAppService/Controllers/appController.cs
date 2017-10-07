using System.Web.Http;
using Microsoft.Azure.Mobile.Server.Config;
using System.Collections.Generic;
using System.Web.UI.WebControls;
using System;
using Newtonsoft.Json.Linq;

namespace asstMobileAppService.Controllers
{
    [MobileAppController]
    public class appController : ApiController
    {
        
        // GET api/app
        public string Get(string account, string password)
        {
            init();
            LoginInfo result = accountTable.Find(x=>x.Account == account);

            if (result == null)
            {
                return "Error 404: Email does not exist!";
            }
            else if (!result.Password.Equals(password))
            {
                return "Error 405:Password does not match!";
            }
            else
            {
                return "Login Success!";
            }
            
        }
      public void init() {
            accountTable.Add(new LoginInfo { Account = "test@gmail.com", Password = "123"});
            accountTable.Add(new LoginInfo { Account = "admin@gmail.com", Password = "admin" });
    }
        public List<LoginInfo> accountTable = new List<LoginInfo>();
    }

    
}
