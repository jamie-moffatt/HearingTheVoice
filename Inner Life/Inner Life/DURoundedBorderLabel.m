//
//  DURoundedBorderLabel.m
//  Durham Life
//
//  Created by Matthew Bates on 22/08/2012.
//  Copyright (C) 2012 Durham University. All Rights Reserved.
//

#import "DURoundedBorderLabel.h"
#import <QuartzCore/QuartzCore.h>

@implementation DURoundedBorderLabel

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    return self;
}

- (void)drawTextInRect:(CGRect)rect
{
    self.layer.cornerRadius = 10;
    [super drawTextInRect: CGRectInset(rect, 10.0, 5.0)];
}

- (void)sizeToFitFixedWidth:(CGFloat)fixedWidth
{
    self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y, fixedWidth, 0);
    self.lineBreakMode = NSLineBreakByWordWrapping;
    self.numberOfLines = 0;
    [self sizeToFit];
    self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y, fixedWidth, self.frame.size.height + 10.0);
}

@end
