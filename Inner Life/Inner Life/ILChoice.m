//
//  ILChoice.m
//  Inner Life
//
//  Created by Matthew Bates on 13/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILChoice.h"

@implementation ILChoice

- (id)initWithText: (NSString*)text andValue: (NSString*)value
{
    if (self = [super init])
    {
        _text = text;
        _value = value;
    }
    
    return self;
}

- (NSString*)description
{
    return [NSString stringWithFormat:@"(%@ => %@)", _text, _value];
}

@end
