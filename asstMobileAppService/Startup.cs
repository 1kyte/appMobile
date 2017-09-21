using Microsoft.Owin;
using Owin;

[assembly: OwinStartup(typeof(asstMobileAppService.Startup))]

namespace asstMobileAppService
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureMobileApp(app);
        }
    }
}