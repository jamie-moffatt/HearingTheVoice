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
        NSMutableArray *sections = [[NSMutableArray alloc] initWithCapacity:10];
        for (int i = 0; i < 10; i++) [sections addObject:[NSNumber numberWithInt:27]];
        
        _sectionSpecification = [sections copy];
        _currentSection = 0;
        _currentSubSection = 0;
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self)
    {
        NSMutableArray *sections = [[NSMutableArray alloc] initWithCapacity:10];
        for (int i = 0; i < 10; i++) [sections addObject:[NSNumber numberWithInt:27]];
        
        _sectionSpecification = [sections copy];
        _currentSection = 0;
        _currentSubSection = 0;
    }
    return self;
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
    CGFloat x = rect.origin.x;
    CGFloat y = rect.origin.y;
    CGFloat width = rect.size.width;
    CGFloat height = rect.size.height;
    
    int numOfSections = [_sectionSpecification count];
    CGFloat sizeOfSegments = width / 10;
    
    if (numOfSections % 2)
    {
        for (int i = 5 - numOfSections/2; i <= 5 + numOfSections/2; i++)
        {
            UIBezierPath* ovalPath = [UIBezierPath bezierPathWithOvalInRect: CGRectMake(i*sizeOfSegments-10, 3, 20, 20)];
            [[UIColor colorWithRed: 0 green: 0 blue: 1 alpha: 1] setFill];
            [ovalPath fill];
        }
    }
    else
    {
        for (int i = 5 - numOfSections/2; i < 5 + numOfSections/2; i++)
        {
            UIBezierPath* ovalPath = [UIBezierPath bezierPathWithOvalInRect: CGRectMake(i*sizeOfSegments-10+sizeOfSegments/2, 5, 20, 20)];
            [[UIColor colorWithRed: 0 green: 0 blue: 1 alpha: 1] setFill];
            [ovalPath fill];
        }
    }
    
    numOfSections = [[_sectionSpecification objectAtIndex:_currentSection] integerValue];
    sizeOfSegments = width / 28;
    
    if (numOfSections % 2)
    {
        for (int i = 14 - numOfSections/2, j = 0; i <= 14 + numOfSections/2; i++, j++)
        {
            UIBezierPath* ovalPath = [UIBezierPath bezierPathWithOvalInRect: CGRectMake(i*sizeOfSegments-4.5, 30, 9, 9)];
            if (j < _currentSubSection) [[UIColor colorWithRed: 1 green: 0 blue: 1 alpha: 1] setFill];
            else [[UIColor colorWithWhite:0.5 alpha:1] setFill];
            [ovalPath fill];
        }
    }
    else
    {
        for (int i = 14 - numOfSections/2, j = 0; i < 14 + numOfSections/2; i++, j++)
        {
            UIBezierPath* ovalPath = [UIBezierPath bezierPathWithOvalInRect: CGRectMake(i*sizeOfSegments-4.5+sizeOfSegments/2, 30, 9, 9)];
            if (j < _currentSubSection) [[UIColor colorWithRed: 1 green: 0 blue: 1 alpha: 1] setFill];
            else [[UIColor colorWithWhite:0.5 alpha:1] setFill];
            [ovalPath fill];
        }
    }

}

@end
