(function()
      {
         var self = this,
             page = document.getElementById("pageIndicatorPage"),
             changer = document.getElementById("hsectionchanger"),
             sectionChanger,
             elPageIndicator = document.getElementById("pageIndicator"),
             pageIndicator,
             pageIndicatorHandler;

         page.addEventListener("pagebeforeshow", function()
         {
            /* Create PageIndicator */
        	pageIndicator =  tau.widget.PageIndicator(elPageIndicator, {numberOfPages: 2, maxPage: 2});
            pageIndicator.setActive(0);

            sectionChanger = new tau.widget.SectionChanger(changer,
            {
               circular: true,
               orientation: "horizontal",
               useBouncingEffect: true
            });
         });

         page.addEventListener("pagehide", function()
         {
            sectionChanger.destroy();
            pageIndicator.destroy();
         });

         /* Indicator setting handler */
         pageIndicatorHandler = function(e)
         {
            pageIndicator.setActive(e.detail.active);
         };

         /* Bind the callback */
         changer.addEventListener("sectionchange", pageIndicatorHandler, false);
      })();