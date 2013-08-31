//
//  ILNotificationClockView.m
//  Inner Life
//
//  Created by Matthew Bates on 18/08/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILNotificationClockView.h"

@implementation ILNotificationClockView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        [self commonInit];
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)decoder
{
    self = [super initWithCoder:decoder];
    if (self)
    {
        [self commonInit];
    }
    return self;
}

-(void)setAmStart:(NSInteger)amStart
{
    _amStart = amStart;
    [self setNeedsDisplay];
}

-(void)setAmEnd:(NSInteger)amEnd
{
    _amEnd = amEnd;
    [self setNeedsDisplay];
}


-(void)setPmStart:(NSInteger)pmStart
{
    _pmStart = pmStart;
    [self setNeedsDisplay];
}

-(void)setPmEnd:(NSInteger)pmEnd
{
    _pmEnd = pmEnd;
    [self setNeedsDisplay];
}

-(void)setTime:(NSInteger)time
{
    _time = time;
    [self setNeedsDisplay];
}

- (void)commonInit
{
    _amStart = 0;
    _amEnd = 5;
    _pmStart = 12;
    _pmEnd = 15;
    clockHand = [UIImage imageNamed:@"ClockHand.png"];
}

- (void)drawRect:(CGRect)rect
{
    CGFloat x = rect.origin.x + 5;
    CGFloat y = rect.origin.y + 5;
    CGFloat width = rect.size.width - 10;
    CGFloat height = rect.size.height - 10;
    CGFloat cx = x + width/2;
    CGFloat cy = y + height/2;
    CGFloat d = MAX(width, height);
    CGFloat r = d/2;
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    [[UIColor colorWithWhite:0.96 alpha:1] setFill];
    
    NSInteger amStartDegree = ((270 + (_amStart * 15)) % 360);
    NSInteger amEndDegree = ((270 + (_amEnd * 15)) % 360);
    
    UIBezierPath* amSelectorPath = [UIBezierPath bezierPath];
    [amSelectorPath addArcWithCenter: CGPointMake(cx, cy) radius: r
                          startAngle: (amStartDegree * M_PI)/180
                            endAngle: (amEndDegree * M_PI)/180 clockwise: YES];
    
    [amSelectorPath addLineToPoint: CGPointMake(cx, cy)];
    [amSelectorPath closePath];
    [amSelectorPath fill];
    
    NSInteger pmStartDegree = ((270 + (_pmStart * 15)) % 360);
    NSInteger pmEndDegree = ((270 + (_pmEnd * 15)) % 360);
    
    UIBezierPath* pmSelectorPath = [UIBezierPath bezierPath];
    [pmSelectorPath addArcWithCenter: CGPointMake(cx, cy) radius: r
                          startAngle: (pmStartDegree * M_PI)/180
                            endAngle: (pmEndDegree * M_PI)/180 clockwise: YES];
    
    [pmSelectorPath addLineToPoint: CGPointMake(cx, cy)];
    [pmSelectorPath closePath];
    [pmSelectorPath fill];

    
    UIBezierPath* path = [UIBezierPath bezierPathWithOvalInRect:CGRectMake(x, y, d, d)];
    [[UIColor colorWithWhite:0.88 alpha:1] setStroke];
    [path stroke];
    
    CGContextSaveGState(context);
    CGContextTranslateCTM(context, x+r, y+r);
    CGFloat hours = ((float)_time / (60*60));
    CGFloat degrees = hours*15 - 90;
    CGContextRotateCTM(context, degrees * M_PI / 180);
    CGContextTranslateCTM(context, -r -x, -r -y);
    [clockHand drawInRect:CGRectMake(x, y, d, d)];
    CGContextRestoreGState(context);
    
    CGContextSaveGState(context);
    UIBezierPath* path2 = [UIBezierPath bezierPath];
    [path2 moveToPoint:CGPointMake(0, r)];
    [path2 addLineToPoint:CGPointMake(0, r-5)];
    
    CGContextTranslateCTM(context, x+r, y+r);
    for (int i = 0; i < 24; i++)
    { 
        CGContextRotateCTM(context, 15 * M_PI / 180);
        [path2 stroke];
        
    }
    CGContextTranslateCTM(context, -r -x, -r-y);
    CGContextRestoreGState(context);
}

@end
