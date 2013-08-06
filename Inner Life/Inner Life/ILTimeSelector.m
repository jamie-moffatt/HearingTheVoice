//
//  ILTimeSelector.m
//  Inner Life
//
//  Created by Matthew Bates on 06/08/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILTimeSelector.h"

CGFloat cross2D(CGPoint u, CGPoint v)
{
    return u.y * v.x - u.x * v.y;
}

CGPoint subtract(CGPoint u, CGPoint v)
{
    return CGPointMake(u.x - v.x, u.y - v.y);
}

@implementation ILTimeSelector

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        [self commonInit];
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self)
    {
        [self commonInit];
    }
    return self;
}

- (void)commonInit
{
    // default range for AM notifications is 09:00 - 12:00
	// default range for PM notifications is 15:00 - 18:00
    amStartTime = 9;
    pmStartTime = 16;
    
    touching = NO;
    
    hit = NO_COLLISION;
}


- (void)drawRect:(CGRect)rect
{
    CGFloat width = rect.size.width;
    CGFloat height = rect.size.height;
    cx = CGRectGetMidX(rect);
    cy = CGRectGetMidY(rect);
    
    CGFloat d = MIN(width, height) - 10;
    CGFloat r = d / 2;
    
    CGRect square = CGRectMake(cx - r, cy - r, d, d);
    
    // Colors
    UIColor* amBackgroundColor = [UIColor colorWithRed: 0.8 green: 0.8 blue: 0.8 alpha: 1];
    UIColor* pmBackgroundColor = [UIColor colorWithRed: 0.667 green: 0.667 blue: 0.667 alpha: 1];
    UIColor* amFill = [UIColor colorWithRed: 0.49 green: 0.635 blue: 0.588 alpha: 1];
    UIColor* pmFill = [UIColor colorWithRed: 0.49 green: 0.518 blue: 0.635 alpha: 1];
    UIColor* selectedFill = [UIColor colorWithRed: 1 green: 0.251 blue: 0.251 alpha: 1];
    
    // Fonts
    UIFont* helvetica = [UIFont fontWithName: @"Helvetica" size: 12];
    
    // AM Background Fill
    UIBezierPath* amBackgroundPath = [UIBezierPath bezierPath];
    [amBackgroundPath addArcWithCenter: CGPointMake(cx, cy) radius: r startAngle: 270 * M_PI/180 endAngle: 90 * M_PI/180 clockwise: YES];
    [amBackgroundPath addLineToPoint: CGPointMake(cx, cy)];
    [amBackgroundPath closePath];
    
    [amBackgroundColor setFill];
    [amBackgroundPath fill];
    
    // PM Background Fill
    UIBezierPath* pmBackgroundPath = [UIBezierPath bezierPath];
    [pmBackgroundPath addArcWithCenter: CGPointMake(cx, cy) radius: r startAngle: 90 * M_PI/180 endAngle: 270 * M_PI/180 clockwise: YES];
    [pmBackgroundPath addLineToPoint: CGPointMake(cx, cy)];
    [pmBackgroundPath closePath];
    
    [pmBackgroundColor setFill];
    [pmBackgroundPath fill];
    
    // AM Selector Fill
    NSInteger amStartDegree = ((270 + (amStartTime * 15)) % 360);
    NSInteger amEndDegree = ((270 + ((amStartTime + 3) * 15)) % 360);
    
    UIBezierPath* amSelectorPath = [UIBezierPath bezierPath];
    [amSelectorPath addArcWithCenter: CGPointMake(cx, cy) radius: r
                          startAngle: (amStartDegree * M_PI)/180
                            endAngle: (amEndDegree * M_PI)/180 clockwise: YES];
    
    [amSelectorPath addLineToPoint: CGPointMake(cx, cy)];
    [amSelectorPath closePath];
    
    if (hit == AM_TRIANGLE_COLLISION) [selectedFill setFill];
    else [amFill setFill];
    [amSelectorPath fill];
    
    amBoundingTriangle[0] = CGPointMake(0, 0);
    amBoundingTriangle[2] = CGPointMake(r * sin(amStartTime * 15 * M_PI / 180),
                                        r * cos(amStartTime * 15 * M_PI / 180));
    amBoundingTriangle[1] = CGPointMake(r * sin((amStartTime + 3) * 15 * M_PI / 180),
                                        r * cos((amStartTime + 3) * 15 * M_PI / 180));
    
    // PM Selector Fill
    NSInteger pmStartDegree = ((270 + (pmStartTime * 15)) % 360);
    NSInteger pmEndDegree = ((270 + ((pmStartTime + 3) * 15)) % 360);
    
    UIBezierPath* pmSelectorPath = [UIBezierPath bezierPath];
    [pmSelectorPath addArcWithCenter: CGPointMake(cx, cy) radius: r
                          startAngle: pmStartDegree * M_PI/180
                            endAngle: pmEndDegree * M_PI/180 clockwise: YES];
    [pmSelectorPath addLineToPoint: CGPointMake(cx, cy)];
    [pmSelectorPath closePath];
    
    if (hit == PM_TRIANGLE_COLLISION) [selectedFill setFill];
    else [pmFill setFill];
    [pmSelectorPath fill];
    
    pmBoundingTriangle[0] = CGPointMake(0, 0);
    pmBoundingTriangle[2] = CGPointMake(r * sin(pmStartTime * 15 * M_PI / 180),
                                        r * cos(pmStartTime * 15 * M_PI / 180));
    pmBoundingTriangle[1] = CGPointMake(r * sin((pmStartTime + 3) * 15 * M_PI / 180),
                                        r * cos((pmStartTime + 3) * 15 * M_PI / 180));
    
    // Border Stroke
    UIBezierPath* borderStrokePath = [UIBezierPath bezierPathWithOvalInRect:square];
    [[UIColor blackColor] setStroke];
    borderStrokePath.lineWidth = 1;
    [borderStrokePath stroke];
    
    
    [[UIColor blackColor] setFill];
    
    int hour = 0;
    
    for (int i = 0; i < 90; i += 15)
    {
        UIBezierPath* clockTickPath = [UIBezierPath bezierPath];
        [clockTickPath moveToPoint:CGPointMake(cx + r * sin(i * M_PI/180), cy - r * cos(i * M_PI/180))];
        [clockTickPath addLineToPoint:CGPointMake(cx + (r * 0.95) * sin(i * M_PI/180), cy - (r * 0.95) * cos(i * M_PI/180))];
        [clockTickPath stroke];
        
        NSString *hourText = [NSString stringWithFormat:@"%d", hour++];
        CGSize textRect = [hourText sizeWithFont:helvetica];
        [hourText drawAtPoint:CGPointMake(cx + (r * 0.85) * sin(i * M_PI/180) - textRect.width / 2, cy - (r * 0.85) * cos(i * M_PI/180) - textRect.height / 2)
                     forWidth:textRect.width withFont:helvetica lineBreakMode:NSLineBreakByCharWrapping];
    }
    for (int i = 90; i >= 0; i -= 15)
    {
        UIBezierPath* clockTickPath = [UIBezierPath bezierPath];
        [clockTickPath moveToPoint:CGPointMake(cx + r * sin(i * M_PI/180), cy + r * cos(i * M_PI/180))];
        [clockTickPath addLineToPoint:CGPointMake(cx + (r * 0.95) * sin(i * M_PI/180), cy + (r * 0.95) * cos(i * M_PI/180))];
        [clockTickPath stroke];
        
        NSString *hourText = [NSString stringWithFormat:@"%d", hour++];
        CGSize textRect = [hourText sizeWithFont:helvetica];
        [hourText drawAtPoint:CGPointMake(cx + (r * 0.85) * sin(i * M_PI/180) - textRect.width / 2, cy + (r * 0.85) * cos(i * M_PI/180) - textRect.height / 2)
                     forWidth:textRect.width withFont:helvetica lineBreakMode:NSLineBreakByCharWrapping];
    }
    for (int i = 15; i < 90; i += 15)
    {
        UIBezierPath* clockTickPath = [UIBezierPath bezierPath];
        [clockTickPath moveToPoint:CGPointMake(cx - r * sin(i * M_PI/180), cy + r * cos(i * M_PI/180))];
        [clockTickPath addLineToPoint:CGPointMake(cx - (r * 0.95) * sin(i * M_PI/180), cy + (r * 0.95) * cos(i * M_PI/180))];
        [clockTickPath stroke];
        
        NSString *hourText = [NSString stringWithFormat:@"%d", hour++];
        CGSize textRect = [hourText sizeWithFont:helvetica];
        [hourText drawAtPoint:CGPointMake(cx - (r * 0.85) * sin(i * M_PI/180) - textRect.width / 2, cy + (r * 0.85) * cos(i * M_PI/180) - textRect.height / 2)
                     forWidth:textRect.width withFont:helvetica lineBreakMode:NSLineBreakByCharWrapping];
    }
    for (int i = 90; i >= 15; i -= 15)
    {
        UIBezierPath* clockTickPath = [UIBezierPath bezierPath];
        [clockTickPath moveToPoint:CGPointMake(cx - r * sin(i * M_PI/180), cy - r * cos(i * M_PI/180))];
        [clockTickPath addLineToPoint:CGPointMake(cx - (r * 0.95) * sin(i * M_PI/180), cy - (r * 0.95) * cos(i * M_PI/180))];
        [clockTickPath stroke];
        
        NSString *hourText = [NSString stringWithFormat:@"%d", hour++];
        CGSize textRect = [hourText sizeWithFont:helvetica];
        [hourText drawAtPoint:CGPointMake(cx - (r * 0.85) * sin(i * M_PI/180) - textRect.width / 2, cy - (r * 0.85) * cos(i * M_PI/180) - textRect.height / 2)
                     forWidth:textRect.width withFont:helvetica lineBreakMode:NSLineBreakByCharWrapping];
    }
    
    // PM Text
    CGRect pMRect = CGRectMake(cx - 35, cy - 15, 30, 14);
    [@"PM" drawInRect: pMRect withFont: helvetica lineBreakMode: NSLineBreakByWordWrapping alignment: NSTextAlignmentCenter];
    
    
    // AM Text
    CGRect aMRect = CGRectMake(cx + 5, cy - 15, 30, 14);
    [@"AM" drawInRect: aMRect withFont: helvetica lineBreakMode: NSLineBreakByWordWrapping alignment: NSTextAlignmentCenter];
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    currentTouchPoint = [(UITouch*)[touches anyObject] locationInView:self];
    
    CGFloat currentX = currentTouchPoint.x;
    CGFloat currentY = currentTouchPoint.y;
    
    CGFloat u = currentX - cx;
    CGFloat v = currentY - cy;
    
    BOOL isLeft = currentX < cx;
    
    touching = YES;
    
    theta = acos(-v / sqrt(u*u + v*v));
    
    if (isLeft) theta = 2* M_PI - theta;
    
    CGPoint p = CGPointMake(currentX - cx, cy - currentY);
    CGPoint a, b, c;
    
    a = amBoundingTriangle[0];
    b = amBoundingTriangle[1];
    c = amBoundingTriangle[2];
    
    if ([ILTimeSelector point:p inTriangle:a :b :c]) hit = AM_TRIANGLE_COLLISION;
    
    a = pmBoundingTriangle[0];
    b = pmBoundingTriangle[1];
    c = pmBoundingTriangle[2];
    
    if ([ILTimeSelector point:p inTriangle:a :b :c]) hit = PM_TRIANGLE_COLLISION;
    
    [self setNeedsDisplay];
}

-(void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    touching = NO;
    hit = NO_COLLISION;
    if (_delegate) [_delegate timeWasChanged:amStartTime :pmStartTime];
    [self setNeedsDisplay];
}

+(BOOL)point :(CGPoint)p inTriangle :(CGPoint)a :(CGPoint)b :(CGPoint)c
{
    if (cross2D(subtract(p, a), subtract(b, a)) < 0) return NO;
    if (cross2D(subtract(p, b), subtract(c, b)) < 0) return NO;
    if (cross2D(subtract(p, c), subtract(a, c)) < 0) return NO;
    return YES;
}

@end
