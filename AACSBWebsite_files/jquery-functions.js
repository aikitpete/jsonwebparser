

// ** Checking window dimensions for size specific functions ** //

function jqUpdateSize(){

	var width = $(window).width();
	
	if (width > 760){
	
		// ** Match Main Rail and Left Rail Heights
		if($('.home').length > 0){
			$('.modBox').matchHeights();
		}
		
	} else {
	
		// ** Stop matching heights
		if($('.home').length > 0){
			$('.modBox').css({'min-height': 0});
		}
		
		var scrollTimer = null;
		$(window).scroll(function(){
			if (scrollTimer) {
				clearTimeout(scrollTimer);
			}
			scrollTimer = setTimeout(pagePosition,50);
		});
	}
	
};

function pagePosition(){
	scrollTimer = null;

	var top = $('#mainContent').offset().top,
		$window = $(window),
		button = $('.toTop');
	
	if ($window.scrollTop() >= top ){
		button.addClass('show');
	} else {
		button.removeClass('show');
	}

};

(function($,sr){

  // debouncing function from John Hann
  // http://unscriptable.com/index.php/2009/03/20/debouncing-javascript-methods/
  var debounce = function (func, threshold, execAsap) {
      var timeout;

      return function debounced () {
          var obj = this, args = arguments;
          function delayed () {
              if (!execAsap)
                  func.apply(obj, args);
              timeout = null;
          };

          if (timeout)
              clearTimeout(timeout);
          else if (execAsap)
              func.apply(obj, args);

          timeout = setTimeout(delayed, threshold || 100);
      };
  }
  // smartresize 
  jQuery.fn[sr] = function(fn){  return fn ? this.bind('resize', debounce(fn)) : this.trigger(sr); };

})(jQuery,'smartresize');


$(document).ready(function(){

	$('.icon-search').on('click', function(e){
		e.preventDefault();
		if($('body').hasClass('bizEd')){
			$('.siteSearch').fadeToggle();
		} else {
			$(this).parent().parent().children('.siteSearch').fadeToggle();
		}
	});

	if($('.icon-menu').length > 0){
		
		$('ul.sf-menu > li.hasSub').hover(function(){
			$(this).toggleClass('sfHover');
		});
	
		$('.mobileMenuIcons .icon-menu').click(function(e){
			var $this = $(this);
			
			e.preventDefault();
			e.stopPropagation();
			
			$this.parent().next().children('.sf-menu').slideToggle('fast');
			$this.toggleClass('active');
		});
	
		$('li.hasSub > a').each(function(){
			var navIcon = $('<span class="show-menu">+</span>');
			$(this).append(navIcon);
		});
		
		$('.subNav').css('display','none'); // Display none it, just for the JS users
		
		$('.mainNav .show-menu').click(function(e){
			var $this = $(this),
				link = $this.parent(),
				li = link.parent();
				
			e.preventDefault();
			e.stopPropagation();
			
			if ($this.hasClass('active')){
				link.next().css('top','55px').slideUp();
				$this.removeClass('active');
				li.removeClass('open');
			} else {
				li.siblings().removeClass('open');
				$('.mainNav .show-menu').removeClass('active').text('+');
				li.siblings().children('.subNav').slideUp();
				
				link.next().css('top','55px').slideDown();
				$this.addClass('active').text('-');
				li.addClass('open');
			}
			
			if($this.hasClass('active')){
				$this.text('-');
			} else {
				$this.text('+');
				$this.parent().blur();
			}
		});
		
		$('.footer .icon-menu').click(function(e){
			var $this = $(this);
			
			e.preventDefault();
			$this.nextAll('ul').slideToggle('fast');
			$this.toggleClass('active');
		});
	}
		
	if($(".slideshow").length > 0){	
		$(".slideshow").each(function(){
			var $slideshow = $(this), 
				slides = $slideshow.find('li'),
				firstSlide = $slideshow.find('li:first-child'),
				firstPager =  $('.slidePager').find('.navDisc:first-child'),
				isChanging = false;
			
			firstSlide.addClass("active");
			
			var timeout = setTimeout(setTimer, 10000);
			
			slides.each(function(){
				var slide = $(this),
					slideInfo = slide.find('.slideInfo'),
					firstSlideInfo = slide.find('.slideInfo:first-child'),
					thisCount = slide.index() + 1,
					pagerNav = $('<a href="#" class="navDisc"> ' + thisCount + ' </a>');
					 
				firstSlideInfo.addClass("active");
				$('.slidePager .navDisc:first-child').addClass('active');
				$('.slidePager').append(pagerNav);
			});
					
			$slideshow.on("click", ".prev, .next, .navDisc", function(e){	
				e.preventDefault();
				if (!isChanging && !$(this).hasClass("active")){
					isChanging = true;
					
					var $active = $slideshow.find(".active"),
						$next;
					
					if ( $(this).hasClass("next") ){
						if ( $active.next(".slide, .navDisc").length > 0 ){
							$next = $active.next(".slide, .navDisc");
						} else {
							$next = $slideshow.find(".slide:first-child, .navDisc:first-child");
						}
					} else if ( $(this).hasClass("prev") ){
						if ( $active.prev(".slide, .navDisc").length > 0 ){
							$next = $active.prev(".slide, .navDisc");
						} else {
							$next = $slideshow.find(".slide:last-child, .navDisc:last-child");
						}
					} else {
						var $this = $(this),            
							thisPos = $this.index(),
							thisSlide = slides.eq(thisPos);
							
						$('.slidePager').children('.navDisc').removeClass("active");
						$this.addClass("active");
						
						$next = thisSlide;
					}
					
					if ($(window).width() > 720){
						$active.find(".slideImg").fadeOut(800);
						$next.find(".slideImg").fadeIn(600, function(){
							isChanging = false; 
						
						});
						
						$active.removeClass("active").addClass("inactive");
						$next.removeClass("inactive").addClass("active");
						
						$active.find(".slideInfo").hide();
						$next.find(".slideInfo").show();
						
					} else {
						$active.removeClass("active").addClass("inactive");
						$next.removeClass("inactive").addClass("active");
						isChanging = false;					
					}
					
					clearTimeout(timeout);
					timeout = setTimeout(setTimer, 10000);
				} 
				
			});

			function setTimer() {
				var nextSlide = $slideshow.find(".next");
					
				nextSlide.trigger("click"); 
				timeout = setTimeout(setTimer, 10000);
			}
			
		});
	}
	
	if($(".bizSlideshow").length > 0){	
		$('.slidesNav').on('click', 'li', function(e){
			var $this = $(this),
				thisPos = $this.index(),
				slides = $('.slides').children('li')
				thisSlide = slides.eq(thisPos),
				slideImages = slides.children('.bizSlideImg'),
				thisSlideImage = thisSlide.children('.bizSlideImg');
		
			e.preventDefault();
			slideImages.hide();
			thisSlideImage.show();
			
			if(!$this.hasClass('active')){
				isChanging = true;
				slideImages.hide();
				$this.siblings('li').removeClass('active');
				$this.addClass('active');
				slides.removeClass('current');
				thisSlide.addClass('current');
				thisSlideImage.fadeIn();
			}
		});
	}
	
	if($(".recentBox").length > 0){	
		var allItems = $('.issuesList li'),
			thisItem = $('.focused'),
			thisText = thisItem.text(),
			thisLink = thisItem.children('a').attr('href');
		
		$('.issueLink').text(thisText).attr('href', thisLink)
		
		$('.issuesList').serialScroll({		
			items:'li',
			prev:'.recentBox .prev', 
			next:'.recentBox .next', 
			axis:'x',
			start:1,
			jump:true,
			offset:-104, 
			duration:500,
			navigatiaon: allItems,
			onBefore:function(e,el,$p,$i,pos){
				var thisItem = allItems.eq(pos);
				
				allItems.removeClass('focused');
				thisItem.addClass('focused');
			},
			onAfter:function(){
			var thisItem = $('.focused'),
				thisText = thisItem.text(),
				thisLink = thisItem.children('a').attr('href');
				$('.issueLink').text(thisText).attr('href', thisLink);
			}
		});

		$('.issuesList li:eq(0)').trigger('click');
	}
	
	if($('#carousel').length > 0){
		$('#carousel').flexslider({
			animation: "slide",
			controlNav: true,
			animationLoop: false,
			slideshow: false,
			itemWidth: 152,
			prevText: "",
			nextText: "",
			asNavFor: '#slider'
		});
		   
		$('#slider').flexslider({
			animation: "slide",
			controlNav: false,
			animationLoop: false,
			slideshow: false,
			itemWidth: 720,
			prevText: "",
			nextText: "",
			sync: "#carousel"
		});
	}
	
	if($('.calendar').length > 0){
		$('.viewCal').click(function(e){
			e.preventDefault();
			$(this).parents('.calendar').removeClass('fullList');
		});
		
		$('.viewList').click(function(e){
			e.preventDefault();
			$(this).parents('.calendar').addClass('fullList');
		});
	}
	
	if($('.multimediaMod').length > 0){
		
		$('.videoPager').append('<div class="videoNav"></div>');
		
		$('.mediaNavMask').serialScroll({		
			items:'ul',
			prev:'.videosPager .prev', 
			next:'.videosPager .next', 
			axis:'x',
			duration:500
		});
		
		$('.mediaNav .itemList').each(function(){
			var $this = $(this),
				items = $(this).children('li');
			
			items.on('click', 'a', function(e){
				var $clicked = $(this),
					thisVid = $(this).attr('href'),
					nowPlaying = $('<p class="current">Now Playing</p>'),
					vidEmbed = $('<embed src="' + thisVid + '" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" />');
				
				e.preventDefault();
				
				if(! $clicked.parent('li').hasClass('on')) {
					items.removeClass('on');
					$('.current').remove();
					$clicked.parent().addClass('on').prepend(nowPlaying);
					$('.mediaPlayer param').attr('value', thisVid);
					$('.mediaPlayer object embed').remove();
					$('.mediaPlayer object').attr('data', thisVid).append(vidEmbed);
				}
			});
		});
	}
	
	if($('.galleryMod').length > 0){
		$('.galleryMod h2 a').click(function(e){
			var $this = $(this);
			
			e.preventDefault();
			$this.parent().parent().next('ul').slideToggle();
			$this.toggleClass('active');
			
			if($this.hasClass('active')){
				$this.children('span').text('-');
			} else {
				$this.children('span').text('+');
			}
			
		});	
	}
	
	jqUpdateSize();
		
});

$(window).smartresize(function(){
	jqUpdateSize();
});