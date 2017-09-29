using System.Web.Http;
using Microsoft.Azure.Mobile.Server.Config;

namespace asstMobileAppService.Controllers
{
    [MobileAppController]
    public class appController : ApiController
    {
        // GET api/app
        public string Get()
        {
            return "Hello from custom controller!";
        }
    }
}
