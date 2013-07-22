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
        
    }
    return self;
}

- (void)drawRect:(CGRect)rect
{
    NSMutableArray *sections = [[NSMutableArray alloc] initWithCapacity:10];
    for (int i = 0; i < 10; i++) [sections addObject:[NSNumber numberWithInt:27]];
    
    _sectionSpecification = [sections copy];
    _currentSection = 0;
    
    CGFloat width = rect.size.width;
    CGFloat height = rect.size.height;
    
    CGFloat centerX = width / 2;
    CGFloat centerY = height / 2;
    
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
        for (int i = 14 - numOfSections/2; i <= 14 + numOfSections/2; i++)
        {
            UIBezierPath* ovalPath = [UIBezierPath bezierPathWithOvalInRect: CGRectMake(i*sizeOfSegments-4.5, 30, 9, 9)];
            [[UIColor colorWithRed: 1 green: 0 blue: 1 alpha: 1] setFill];
            [ovalPath fill];
        }
    }
    else
    {
        for (int i = 14 - numOfSections/2; i < 14 + numOfSections/2; i++)
        {
            UIBezierPath* ovalPath = [UIBezierPath bezierPathWithOvalInRect: CGRectMake(i*sizeOfSegments-4.5+sizeOfSegments/2, 30, 20, 20)];
            [[UIColor colorWithRed: 1 green: 0 blue: 1 alpha: 1] setFill];
            [ovalPath fill];
        }
    }

}

@end
