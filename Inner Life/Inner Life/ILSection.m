//
//  ILSection.m
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILSection.h"

@implementation ILSection

- (NSString*)description
{
    return [NSString stringWithFormat:@"(%ld,%@)", (long)_sectionID, _name];
}

@end
