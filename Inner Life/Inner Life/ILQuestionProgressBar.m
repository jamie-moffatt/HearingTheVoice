//
//  ILQuestionProgressBar.m
//  Inner Life
//
//  Created by Matthew Bates on 22/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILQuestionProgressBar.h"

@implementation ILQuestionProgressBar

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
    NSMutableArray *sections = [[NSMutableArray alloc] initWithCapacity:10];
    for (int i = 0; i < 10; i++) [sections addObject:[NSNumber numberWithInt:27]];
    
    _sectionSpecification = [sections copy];
    _currentSection = 0;
    _currentSubSection = 0;
    
    current = [UIImage imageNamed:@"section_current.png"];
    complete = [UIImage imageNamed:@"section_complete.png"];
    incomplete = [UIImage imageNamed:@"section_incomplete.png"];
}

 - (void)setSectionSpecification:(NSArray *)sectionSpecification
{
    _sectionSpecification = sectionSpecification;
    [self setNeedsDisplay];
}

- (void)setCurrentSection:(NSInteger)currentSection
{
    _currentSection = currentSection;
    [self setNeedsDisplay];
}

- (void)setCurrentSubSection:(NSInteger)currentSubSection
{
    _currentSubSection = currentSubSection;
    [self setNeedsDisplay];
}

- (void)drawRect:(CGRect)rect
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGFloat width = rect.size.width;
    
    NSInteger numOfSections = [_sectionSpecification count];
    CGFloat sizeOfSegments = width / 10;
    
    NSArray* gradientColors = [NSArray arrayWithObjects:
                               (id)[UIColor colorWithWhite:0 alpha:1].CGColor,
                               (id)[UIColor colorWithWhite:0 alpha:1].CGColor,
                               (id)[UIColor colorWithWhite:0.75 alpha:1].CGColor,
                               (id)[UIColor colorWithWhite:0.75 alpha:1].CGColor, nil];
    CGFloat gradientLocations[] = {0, _currentSection/(float)numOfSections, MIN((_currentSection + 1)/(float)numOfSections, 1), 1};
    CGGradientRef gradient = CGGradientCreateWithColors(colorSpace, (__bridge CFArrayRef)gradientColors, gradientLocations);
    
    CGContextSaveGState(context);
    if (numOfSections % 2)
    {
        for (NSInteger i = 5 - numOfSections/2, j = 0; i <= 5 + numOfSections/2; i++, j++)
        {
            CGContextAddPath(context, [UIBezierPath bezierPathWithOvalInRect:CGRectMake(i*sizeOfSegments-11, 4, 22, 22)].CGPath);
        }
    }
    else
    {
        for (NSInteger i = 5 - numOfSections/2, j = 0; i < 5 + numOfSections/2; i++, j++)
        {
            CGContextAddPath(context, [UIBezierPath bezierPathWithOvalInRect:CGRectMake(i*sizeOfSegments-11+sizeOfSegments/2, 4, 22, 22)].CGPath);
        }
    }
    
    CGFloat cx0;
    CGFloat cx1;
    CGFloat cy0 = 15;
    if (numOfSections % 2)
    {
        cx0 = (5 - numOfSections/2)*sizeOfSegments;
        cx1 = (5 + numOfSections/2)*sizeOfSegments;
        CGContextAddRect(context, CGRectMake(cx0, cy0 - 4, cx1 - cx0, 8));
    }
    else
    {
        cx0 = (5 - numOfSections/2)*sizeOfSegments + sizeOfSegments/2;
        cx1 = (5 + numOfSections/2)*sizeOfSegments + sizeOfSegments/2;
         CGContextAddRect(context, CGRectMake(cx0, cy0 - 4, cx1 - cx0, 8));
    }
    
    CGContextClip(context);
    CGContextDrawLinearGradient(context, gradient, CGPointMake(cx0-11, cy0), CGPointMake(cx1+11, cy0), 0);
    CGContextRestoreGState(context);
    
    if (numOfSections % 2)
    {
        for (NSInteger i = 5 - numOfSections/2, j = 0; i <= 5 + numOfSections/2; i++, j++)
        {
            if (j < _currentSection) [complete drawInRect:CGRectMake(i*sizeOfSegments-10, 5, 20, 20)];
            else if (j == _currentSection) [current drawInRect:CGRectMake(i*sizeOfSegments-10, 5, 20, 20)];
            else [incomplete drawInRect:CGRectMake(i*sizeOfSegments-10, 5, 20, 20)];
        }
    }
    else
    {
        for (NSInteger i = 5 - numOfSections/2, j = 0; i < 5 + numOfSections/2; i++, j++)
        {
            if (j < _currentSection) [complete drawInRect:CGRectMake(i*sizeOfSegments-10+sizeOfSegments/2, 5, 20, 20)];
            else if (j == _currentSection) [current drawInRect:CGRectMake(i*sizeOfSegments-10+sizeOfSegments/2, 5, 20, 20)];
            else [incomplete drawInRect:CGRectMake(i*sizeOfSegments-10+sizeOfSegments/2, 5, 20, 20)];
        }
    }
    
    numOfSections = [[_sectionSpecification objectAtIndex:_currentSection] integerValue];
    sizeOfSegments = width / 28;
    
    if (numOfSections % 2)
    {
        for (NSInteger i = 14 - numOfSections/2, j = 0; i <= 14 + numOfSections/2; i++, j++)
        {
            UIBezierPath* ovalPath = [UIBezierPath bezierPathWithOvalInRect: CGRectMake(i*sizeOfSegments-4.5, 30, 9, 9)];
            if (j < _currentSubSection) [[UIColor colorWithWhite:0.5 alpha: 1] setFill];
            else if (j == _currentSubSection) [[UIColor colorWithWhite:0.2 alpha: 1] setFill];
            else [[UIColor colorWithWhite:0.85 alpha:1] setFill];
            [ovalPath fill];
        }
    }
    else
    {
        for (NSInteger i = 14 - numOfSections/2, j = 0; i < 14 + numOfSections/2; i++, j++)
        {
            UIBezierPath* ovalPath = [UIBezierPath bezierPathWithOvalInRect: CGRectMake(i*sizeOfSegments-4.5+sizeOfSegments/2, 30, 9, 9)];
            if (j < _currentSubSection) [[UIColor colorWithWhite:0.5 alpha: 1] setFill];
            else if (j == _currentSubSection) [[UIColor colorWithWhite:0.2 alpha: 1] setFill];
            else [[UIColor colorWithWhite:0.85 alpha:1] setFill];
            [ovalPath fill];
        }
    }

}

@end
