//
//  ILQuestion.m
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILQuestion.h"

@implementation ILQuestion

- (NSString*)description
{
    return [NSString stringWithFormat:@"(%d,%d)", _questionID, _number];
}

+ (ILQuestionType) typeFromString: (NSString*)string
{
    if ([string isEqualToString:@"radio"])
    {
        return RADIO;
    }
    else if ([string isEqualToString:@"yesno"])
    {
        return YESNO;
    }
    else if ([string isEqualToString:@"numscale"])
    {
        return NUMSCALE;
    }
    else
    {
        return RADIO;
    }
}

@end
