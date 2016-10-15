$.fn.setmoreSlider	=	function(options)
{
	this.state			=	 options ? options.defaultState ? options.defaultState : "no" : "no";
	this.sliderSwitch	=	".slider_switch";
	this.bothSliders	=	".sliders";
	this.sliderVal		=	".sliderVal";
	this.sliderValClass	=	"sliderVal";
	this.switchStruct	=	'<span class="slider_off sliders"> OFF </span><span class="slider_on sliders"> ON </span><span class="slider_switch"></span>';
	var self			=	this;

	this.init		=	function()
	{
		if(!$(this).hasClass("sliderdone"))
		{
			this.bindEvents();
			this.createRequiredElements();

			$(this).addClass("sliderdone");
		}
		this.changeState(this, this.state,true);
	};

	this.changeState			=	function(elem, currentState,isFirst)
	{
		elem.state	=	currentState;
		if($(elem).find(this.sliderVal).val() != elem.state)
		{
			if(elem.state=="yes")
			{
				self.slideRight(elem);
			}
			else
			{
				if(!isFirst)
					self.slideLeft(elem);
				else
				{
					$(elem).find(this.bothSliders).css({left : "-=59"});
					$(elem).find(this.sliderSwitch).css({left : "-=37"});
				}
			}
			$(elem).find(this.sliderVal).val(elem.state);
		}
	};

	this.bindEvents				=	function()
	{
		$(this).unbind('click');
		$(this).bind('click',function()
		{
			if($(this).find(self.sliderVal).val() == "yes")
			{
				self.changeState(this, "no");
			}
			else
			{
				self.changeState(this, "yes");
			}
		});
	};

	this.slideLeft				=	function(elem)
	{
		$(elem).find(this.bothSliders).animate({left : "-=59"},120);
		$(elem).find(this.sliderSwitch).animate({left : "-=37"},100);
	};

	this.slideRight				=	function(elem)
	{
		if( parseInt($(elem).find(this.bothSliders).css("left") , 10) < 50 )
		{
			$(elem).find(this.bothSliders).animate({left : "+=59"},120);
			$(elem).find(this.sliderSwitch).animate({left : "+=37"},100);
		}
	};

	this.createRequiredElements	=	function()
	{
		$(this).append("<input type='hidden' class='"+this.sliderValClass+"' value='' />");
		$(this).append(this.switchStruct);
	};

	this.init();
}