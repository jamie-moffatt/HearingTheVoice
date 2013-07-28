//
//  ILChoice.m
//  Inner Life
//
//  Created by Matthew Bates on 13/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILChoice.h"

@implementation ILChoice

static const ILChoice* NOT_AVAILABLE;

- (id)initWithText: (NSString*)text andValue: (NSString*)value
{
    if (self = [super init])
    {
        _text = text;
        _value = value;
    }
    return self;
}

- (NSString *)description
{
    return [NSString stringWithFormat:@"(%@ => %@)", _text, _value];
}

+ (const ILChoice *)NA
{
    if (NOT_AVAILABLE)
    {
        return NOT_AVAILABLE;
    }
    else
    {
        NOT_AVAILABLE = [[ILChoice alloc] init];
        NOT_AVAILABLE.text = @"N/A";
        NOT_AVAILABLE.value = @"N/A";
        return NOT_AVAILABLE;
    }
}

@end
