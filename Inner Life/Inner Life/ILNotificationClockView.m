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

-(void)setD:(NSInteger)d
{
    _d = d;
    [self setNeedsDisplay];
}

- (void)commonInit
{
    clockHand = [UIImage imageNamed:@"ClockHand.png"];
}

- (void)drawRect:(CGRect)rect
{
    CGFloat x = rect.origin.x;
    CGFloat y = rect.origin.y;
    CGFloat width = rect.size.width;
    CGFloat height = rect.size.height;
    CGFloat cx = x + width/2;
    CGFloat cy = y + height/2;
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    UIBezierPath* path = [UIBezierPath bezierPathWithOvalInRect:CGRectMake(cx, cy, width/2, height/2)];
    [path fill];
    
    CGContextRotateCTM(context, _d * M_PI / 180);
    [clockHand drawInRect:rect];
}

@end
