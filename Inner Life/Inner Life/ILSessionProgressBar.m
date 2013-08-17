//
//  ILSessionProgressBar.m
//  Inner Life
//
//  Created by Matthew Bates on 16/08/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILSessionProgressBar.h"

@implementation ILSessionProgressBar

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

- (void)commonInit
{
    int defaultNumOfSegments = 28;
    NSMutableArray* xs = [[NSMutableArray alloc] initWithCapacity:defaultNumOfSegments];
    for (int i = 0; i < defaultNumOfSegments; i++) [xs addObject:[NSNumber numberWithInt:2]];
    _segmentMap = [xs copy];
}

- (void)setSegmentMap:(NSArray *)segmentMap
{
    _segmentMap = segmentMap;
    [self setNeedsDisplay];
}


- (void)drawRect:(CGRect)rect
{
    CGFloat x = rect.origin.x + 1;
    CGFloat y = rect.origin.y + 1;
    CGFloat width = rect.size.width - 2;
    CGFloat height = rect.size.height - 2;
    
    CGFloat sw = width / [_segmentMap count];
    
    //// General Declarations
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    //// Gradient Declarations
    NSArray* filledGradientColors = [NSArray arrayWithObjects:
                                     (id)[UIColor colorWithRed: 0.139 green: 0.289 blue: 0.702 alpha: 1].CGColor,
                                     (id)[UIColor colorWithRed: 0.241 green: 0.426 blue: 0.851 alpha: 1].CGColor,
                                     (id)[UIColor colorWithRed: 0.343 green: 0.562 blue: 1 alpha: 1].CGColor, nil];
    CGFloat filledGradientLocations[] = {0, 0.8, 1};
    CGGradientRef filledGradient = CGGradientCreateWithColors(colorSpace, (__bridge CFArrayRef)filledGradientColors, filledGradientLocations);
    
    NSArray* unfilledGradientColors = [NSArray arrayWithObjects:
                                       (id)[UIColor colorWithWhite: 0.65 alpha: 1].CGColor,
                                       (id)[UIColor colorWithWhite: 0.80 alpha: 1].CGColor,
                                       (id)[UIColor colorWithWhite: 1.00 alpha: 1].CGColor, nil];
    CGFloat unfilledGradientLocations[] = {0, 0.8, 1};
    CGGradientRef unfilledGradient = CGGradientCreateWithColors(colorSpace, (__bridge CFArrayRef)unfilledGradientColors, unfilledGradientLocations);
    
    NSArray* shadowGradientColors = [NSArray arrayWithObjects:
                                     (id)[UIColor lightGrayColor].CGColor,
                                     (id)[UIColor colorWithWhite:0.95 alpha:1].CGColor, nil];
    CGFloat shadowGradientLocations[] = {0, 1};
    CGGradientRef shadowGradient = CGGradientCreateWithColors(colorSpace, (__bridge CFArrayRef)shadowGradientColors, shadowGradientLocations);
    
    NSArray* highlightGradientColors = [NSArray arrayWithObjects:
                                        (id)[UIColor colorWithWhite:1 alpha: 0.7].CGColor,
                                        (id)[UIColor colorWithWhite:1 alpha: 0.2].CGColor, nil];
    CGFloat highlightGradientLocations[] = {0, 1};
    CGGradientRef highlightGradient = CGGradientCreateWithColors(colorSpace, (__bridge CFArrayRef)highlightGradientColors, highlightGradientLocations);
    
    //// Outline Drawing
    UIBezierPath* outlinePath = [UIBezierPath bezierPath];
    [outlinePath moveToPoint: CGPointMake(x+(height/2), y)];
    [outlinePath addLineToPoint: CGPointMake(x+width-(height/2), y)];
    [outlinePath addCurveToPoint: CGPointMake(x+width, y+height/2) controlPoint1: CGPointMake(x+width-(height/2), y) controlPoint2: CGPointMake(x+width, y)];
    [outlinePath addCurveToPoint: CGPointMake(x+width-(height/2), y+height) controlPoint1: CGPointMake(x+width, y+height) controlPoint2: CGPointMake(x+width-(height/2), y+height)];
    [outlinePath addLineToPoint: CGPointMake(x+(height/2), y+height)];
    [outlinePath addCurveToPoint: CGPointMake(x, y+height/2) controlPoint1: CGPointMake(x+(height/2), y+height) controlPoint2: CGPointMake(x, y+height)];
    [outlinePath addCurveToPoint: CGPointMake(x+(height/2), y) controlPoint1: CGPointMake(x, y) controlPoint2: CGPointMake(x+(height/2), y)];
    [outlinePath closePath];
    
    //// BackShadow Drawing
    UIBezierPath* backShadowPath = [UIBezierPath bezierPath];
    [backShadowPath moveToPoint: CGPointMake(x+(height/2), y)];
    [backShadowPath addCurveToPoint: CGPointMake(x, y+height/2) controlPoint1:CGPointMake(x+height, y) controlPoint2:CGPointMake(x, y)];
    [backShadowPath addLineToPoint: CGPointMake(x+width, y+height/2)];
    [backShadowPath addCurveToPoint: CGPointMake(x+width-(height/2), y) controlPoint1: CGPointMake(x+width, y+height/2) controlPoint2: CGPointMake(x+width, y)];
    [backShadowPath addCurveToPoint: CGPointMake(x+sw, y) controlPoint1: CGPointMake(x+width-10, y) controlPoint2: CGPointMake(x+sw, y)];
    [backShadowPath closePath];
    CGContextSaveGState(context);
    [backShadowPath addClip];
    CGContextDrawLinearGradient(context, shadowGradient, CGPointMake(x+(width/2), y), CGPointMake(x+(width/2), y+height/2), 0);
    CGContextRestoreGState(context);
    
    //// LeftCurve Drawing
    for (int i = 0; i < [_segmentMap count]; i++)
    {
        NSInteger sc = [[_segmentMap objectAtIndex:i] integerValue];
        
        if (i == 0 && sc)
        {
            UIBezierPath* leftCurvePath = [UIBezierPath bezierPath];
            [leftCurvePath moveToPoint: CGPointMake(x+sw, y)];
            [leftCurvePath addLineToPoint: CGPointMake(x+sw, y+height)];
            [leftCurvePath addLineToPoint: CGPointMake(x+height/2, y+height)];
            [leftCurvePath addCurveToPoint: CGPointMake(x, y+height/2) controlPoint1: CGPointMake(x+(height/2), y+height) controlPoint2: CGPointMake(x, y+height)];
            [leftCurvePath addCurveToPoint: CGPointMake(x+(height/2), y) controlPoint1: CGPointMake(x, y) controlPoint2: CGPointMake(x+(height/2), y)];
            [leftCurvePath addLineToPoint: CGPointMake(x+sw, y)];
            [leftCurvePath closePath];
            CGContextSaveGState(context);
            [leftCurvePath addClip];
            [outlinePath addClip];
            CGContextDrawLinearGradient(context, sc == 2 ? unfilledGradient : filledGradient, CGPointMake(x + sw/2, y), CGPointMake(x +sw/2, y+height), 0);
            CGContextRestoreGState(context);
            
            UIBezierPath* leftCurveHighlightPath = [UIBezierPath bezierPath];
            [leftCurveHighlightPath moveToPoint: CGPointMake(x+sw, y+1)];
            [leftCurveHighlightPath addLineToPoint: CGPointMake(x+sw, y+height/2)];
            [leftCurveHighlightPath addLineToPoint: CGPointMake(x+height/2, y+height/2)];
            [leftCurveHighlightPath addCurveToPoint: CGPointMake(x, y+height/2) controlPoint1: CGPointMake(x+(height/2), y+height/2) controlPoint2: CGPointMake(x, y+height/2)];
            [leftCurveHighlightPath addCurveToPoint: CGPointMake(x+(height/2), y+1) controlPoint1: CGPointMake(x, y) controlPoint2: CGPointMake(x+(height/2), y+1)];
            [leftCurveHighlightPath addLineToPoint: CGPointMake(x+sw, y+1)];
            [leftCurveHighlightPath closePath];
            CGContextSaveGState(context);
            [leftCurveHighlightPath addClip];
            [outlinePath addClip];
            CGContextDrawLinearGradient(context, highlightGradient, CGPointMake(x + sw/2, y+1), CGPointMake(x +sw/2, y+height), 0);
            CGContextRestoreGState(context);
        }
        else if (i == [_segmentMap count] - 1 && sc)
        {
            UIBezierPath* rightCurvePath = [UIBezierPath bezierPath];
            [rightCurvePath moveToPoint: CGPointMake(x+sw*i, y)];
            [rightCurvePath addLineToPoint: CGPointMake(x+sw*i, y+height)];
            [rightCurvePath addLineToPoint: CGPointMake(x+width-height/2, y+height)];
            [rightCurvePath addCurveToPoint: CGPointMake(x+width, y+height/2) controlPoint1: CGPointMake(x+width-(height/2), y+height) controlPoint2: CGPointMake(x+width, y+height)];
            [rightCurvePath addCurveToPoint: CGPointMake(x+width-(height/2), y) controlPoint1: CGPointMake(x+width, y) controlPoint2: CGPointMake(x+width-(height/2), y)];
            [rightCurvePath addLineToPoint: CGPointMake(x+sw*i, y)];
            [rightCurvePath closePath];
            CGContextSaveGState(context);
            [rightCurvePath addClip];
            [outlinePath addClip];
            CGContextDrawLinearGradient(context, sc == 2 ? unfilledGradient : filledGradient, CGPointMake(x + sw/2, y), CGPointMake(x +sw/2, y+height), 0);
            CGContextRestoreGState(context);
            
            UIBezierPath* rightCurveHightlightPath = [UIBezierPath bezierPath];
            [rightCurveHightlightPath moveToPoint: CGPointMake(x+sw*i, y+1)];
            [rightCurveHightlightPath addLineToPoint: CGPointMake(x+sw*i, y+height/2)];
            [rightCurveHightlightPath addLineToPoint: CGPointMake(x+width-height/2, y+height/2)];
            [rightCurveHightlightPath addCurveToPoint: CGPointMake(x+width, y+height/2) controlPoint1: CGPointMake(x+width-(height/2), y+height/2) controlPoint2: CGPointMake(x+width, y+height/2)];
            [rightCurveHightlightPath addCurveToPoint: CGPointMake(x+width-(height/2), y+1) controlPoint1: CGPointMake(x+width, y+1) controlPoint2: CGPointMake(x+width-(height/2), y+1)];
            [rightCurveHightlightPath addLineToPoint: CGPointMake(x+sw*i, y+1)];
            [rightCurveHightlightPath closePath];
            CGContextSaveGState(context);
            [rightCurveHightlightPath addClip];
            [outlinePath addClip];
            CGContextDrawLinearGradient(context, highlightGradient, CGPointMake(x + sw/2, y+1), CGPointMake(x +sw/2, y+height), 0);
            CGContextRestoreGState(context);
        }
        else if (sc)
        {
            UIBezierPath* middleSquarePath = [UIBezierPath bezierPath];
            [middleSquarePath moveToPoint: CGPointMake(x+sw*i, y)];
            [middleSquarePath addLineToPoint: CGPointMake(x+sw*i, y+height)];
            [middleSquarePath addLineToPoint: CGPointMake(x+sw*i+sw, y+height)];
            [middleSquarePath addLineToPoint: CGPointMake(x+sw*i+sw, y)];
            [middleSquarePath closePath];
            CGContextSaveGState(context);
            [middleSquarePath addClip];
            [outlinePath addClip];
            CGContextDrawLinearGradient(context, sc == 2 ? unfilledGradient : filledGradient, CGPointMake(x + sw/2, y), CGPointMake(x +sw/2, y+height), 0);
            CGContextRestoreGState(context);
            
            UIBezierPath* middleSquareHighlightPath = [UIBezierPath bezierPath];
            [middleSquareHighlightPath moveToPoint: CGPointMake(x+sw*i, y+1)];
            [middleSquareHighlightPath addLineToPoint: CGPointMake(x+sw*i, y+height/2)];
            [middleSquareHighlightPath addLineToPoint: CGPointMake(x+sw*i+sw, y+height/2)];
            [middleSquareHighlightPath addLineToPoint: CGPointMake(x+sw*i+sw, y+1)];
            [middleSquareHighlightPath closePath];
            CGContextSaveGState(context);
            [middleSquareHighlightPath addClip];
            [outlinePath addClip];
            CGContextDrawLinearGradient(context, highlightGradient, CGPointMake(x + sw/2, y), CGPointMake(x +sw/2, y+height), 0);
            CGContextRestoreGState(context);
        }
    }
    
    for (int i = 1; i < [_segmentMap count]; i ++)
    {
        UIBezierPath* bezierPath = [UIBezierPath bezierPath];
        [bezierPath moveToPoint: CGPointMake(x+i*sw, y)];
        [bezierPath addLineToPoint: CGPointMake(x+i*sw, y+height)];
        [[UIColor grayColor] setStroke];
        bezierPath.lineWidth = 1;
        CGContextSaveGState(context);
        [outlinePath addClip];
        [bezierPath stroke];
        CGContextRestoreGState(context);
        
    }
    
    
    [[UIColor grayColor] setStroke];
    outlinePath.lineWidth = 1;
    [outlinePath stroke];
    
    
    //// Cleanup
    CGGradientRelease(highlightGradient);
    CGGradientRelease(filledGradient);
    CGGradientRelease(shadowGradient);
    CGColorSpaceRelease(colorSpace);
    
    
}

@end
